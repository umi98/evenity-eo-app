package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EventSchedulerServiceImpl {
    private final EventRepository eventRepository;
    private final EventDetailRepository eventDetailRepository;

    @Async
    public CompletableFuture<Void> runAsyncTask() {
        try {
            autoRejectPendingEventDetails();
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
            Page<EventDetail> pendingEventDetails = eventDetailRepository.findByApprovalStatusAndCreatedDateBefore(
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

                if (now.isAfter(eventStart) && now.isBefore(eventEnd)) {
                    // Set EventDetails to ON_PROGRESS if within event time range
                    eventDetailRepository.updateEventProgress(event.getId(), EventProgress.ON_PROGRESS);
                } else if (now.isAfter(eventEnd)) {
                    // Set EventDetails to FINISHED if event has ended
                    eventDetailRepository.updateEventProgress(event.getId(), EventProgress.FINISHED);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
        }
    }
}
