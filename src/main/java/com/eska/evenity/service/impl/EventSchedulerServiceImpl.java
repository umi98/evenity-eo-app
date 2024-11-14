package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.entity.*;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.repository.EventRepository;
import com.eska.evenity.repository.InvoiceDetailRepository;
import com.eska.evenity.repository.InvoiceRepository;
import com.eska.evenity.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EventSchedulerServiceImpl {
    private final EventRepository eventRepository;
    private final EventDetailRepository eventDetailRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final TransactionService transactionService;

    @Async
    public CompletableFuture<Void> runAsyncTask() {
        try {
            autoRejectPendingEventDetails();
            changeProgressionStatus();
            processBalanceTransfers();
            checkAndCancelUnpaidEvents();
        } catch (Exception e) {
            System.err.println("Error in async task: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Scheduled(fixedRate = 3600000)
    public void autoRejectPendingEventDetails() {
        try {
            Pageable pageable = PageRequest.of(0, 1000);
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            Page<EventDetail> pendingEventDetails = eventDetailRepository.findByApprovalStatusAndCreatedDateAfter(
                    ApprovalStatus.PENDING, twentyFourHoursAgo, pageable
            );
            for (EventDetail eventDetail : pendingEventDetails) {
                eventDetail.setApprovalStatus(ApprovalStatus.REJECTED);
                eventDetail.setModifiedDate(LocalDateTime.now());
                eventDetailRepository.saveAndFlush(eventDetail);
            }
        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 43200000)
    public void changeProgressionStatus() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Event> events = eventRepository.findByIsDeleted(false);

            for (Event event : events) {
                LocalDateTime eventStart = LocalDateTime.of(event.getStartDate(), event.getStartTime());
                LocalDateTime eventEnd = LocalDateTime.of(event.getEndDate(), event.getEndTime());
                List<EventDetail> eventDetails = eventDetailRepository.findByEventId(event.getId());
                List<EventDetail> editedDetails = new ArrayList<>();

                if (event.getIsCancelled()) {
                    eventDetails.forEach(eventDetail -> {
                        eventDetail.setEventProgress(EventProgress.CANCELLED);
                        eventDetail.setModifiedDate(LocalDateTime.now());
                        editedDetails.add(eventDetail);
                    });
                    eventDetailRepository.saveAllAndFlush(editedDetails);
                }
                else if (now.isAfter(eventStart) && now.isBefore(eventEnd)) {
                    // Set EventDetails to ON_PROGRESS if within event time range
                    eventDetails.forEach(eventDetail -> {
                        eventDetail.setEventProgress(EventProgress.ON_PROGRESS);
                        eventDetail.setModifiedDate(LocalDateTime.now());
                        editedDetails.add(eventDetail);
                    });
                    eventDetailRepository.saveAllAndFlush(editedDetails);
                } else if (now.isAfter(eventEnd)) {
                    // Set EventDetails to FINISHED if event has ended
                    eventDetails.forEach(eventDetail -> {
                        eventDetail.setEventProgress(EventProgress.FINISHED);
                        eventDetail.setModifiedDate(LocalDateTime.now());
                        editedDetails.add(eventDetail);
                    });
                    eventDetailRepository.saveAllAndFlush(editedDetails);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processBalanceTransfers() {
        try {
            LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
            // Find eligible unpaid invoice details with finished events older than 3 days and approved status
            List<InvoiceDetail> eligibleInvoiceDetails = invoiceDetailRepository.findEligibleForTransfer(threeDaysAgo);
            eligibleInvoiceDetails.forEach(invoiceDetail -> {
                Invoice invoice = invoiceDetail.getInvoice();
                Event event = invoice.getEvent();

                if (invoice.getStatus() == PaymentStatus.COMPLETE &&
                        event.getEndDate().atTime(event.getEndTime()).isBefore(threeDaysAgo) &&
                        invoiceDetail.getStatus() == PaymentStatus.UNPAID &&
                        invoiceDetail.getEventDetail().getApprovalStatus() == ApprovalStatus.APPROVED) {
                    invoiceDetail.setStatus(PaymentStatus.COMPLETE);
                    invoiceDetail.setModifiedDate(LocalDateTime.now());
                    invoiceDetailRepository.saveAndFlush(invoiceDetail);
                    Long transferAmount = (long) (invoiceDetail.getEventDetail().getCost() * 0.5);
                    transactionService.changeBalanceWhenTransfer(transferAmount, invoiceDetail.getEventDetail());
                }
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndCancelUnpaidEvents() {
        try {
            List<Event> eventList = eventRepository.findByIsCancelledFalseAndIsDeletedFalse();

            eventList.forEach(event -> {
                HashSet<String> categoryList = new HashSet<>();
                List<EventDetail> eventDetails = eventDetailRepository.findByEventId(event.getId());
                Invoice invoice = invoiceRepository.findByEventId(event.getId());
                if (invoice == null || invoice.getStatus() == PaymentStatus.COMPLETE) {
                    return;
                }
                eventDetails.forEach(eventDetail -> {
                    categoryList.add(eventDetail.getProduct().getCategory().getId());
                });
                List<InvoiceDetail> invoiceDetailList = invoiceDetailRepository
                        .findByInvoice_IdOrderByCreatedDateDesc(invoice.getId());

                if (invoiceDetailList.size() == categoryList.size()) {
                    InvoiceDetail firstInvoiceDetail = invoiceDetailList.get(0);

                    LocalDateTime createdDate = firstInvoiceDetail.getCreatedDate();
                    LocalDateTime now = LocalDateTime.now();
                    long daysBetween = java.time.Duration.between(createdDate, now).toDays();

                    if (daysBetween > 3) {
                        event.setIsCancelled(true);
                        eventRepository.saveAndFlush(event);
                    }
                }
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong");
        }
    }

}
