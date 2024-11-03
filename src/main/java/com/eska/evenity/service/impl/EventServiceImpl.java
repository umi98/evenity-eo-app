package com.eska.evenity.service.impl;

import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.TransactionDetail;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.Invoice;
import com.eska.evenity.repository.EventRepository;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.EventDetailService;
import com.eska.evenity.service.EventService;
import com.eska.evenity.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CustomerService customerService;
    private final EventDetailService eventDetailService;
    private final InvoiceService invoiceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EventResponse addNewEvent(EventRequest request) {
        try {
            Customer customer = customerService.getCustomerByCustomerId(request.getCustomerId());
            Event newEvent = Event.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .startDate(request.getStartDate())
                    .startTime(request.getStartTime())
                    .endDate(request.getEndDate())
                    .endTime(request.getEndTime())
                    .province(request.getProvince())
                    .city(request.getCity())
                    .district(request.getDistrict())
                    .address(request.getAddress())
                    .theme(request.getTheme())
                    .participant(request.getParticipant())
                    .customer(customer)
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            eventRepository.saveAndFlush(newEvent);
            eventDetailService.addBulk(request.getEventDetail(), newEvent);
            invoiceService.createInvoice(newEvent);
            return mapToResponse(newEvent, "0");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public EventResponse submitOtherProductUsingEventId(String id, EventRequest request) {
        try {
            Event event = findByIdOrThrowNotFound(id);
            event.setModifiedDate(LocalDateTime.now());
            eventRepository.saveAndFlush(event);
            eventDetailService.addBulk(request.getEventDetail(), event);
            return mapToResponse(event, "0");
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TransactionDetail getTransactionByInvoiceId(String invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        Customer customer = customerService.getCustomerByCustomerId(invoice.getEvent().getCustomer().getId());
        CustomerResponse customerResponse = CustomerResponse.builder()
                .customerId(customer.getId())
                .email(customer.getUserCredential().getUsername())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .province(customer.getProvince())
                .city(customer.getCity())
                .district(customer.getDistrict())
                .address(customer.getAddress())
                .createdDate(customer.getCreatedDate())
                .modifiedDate(customer.getModifiedDate())
                .build();
        Event event = findByIdOrThrowNotFound(invoice.getEvent().getId());
        EventResponse eventResponse = mapToResponse(event, "1");
        return TransactionDetail.builder()
                .eventResponse(eventResponse)
                .customerResponse(customerResponse)
                .build();
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> result = eventRepository.findAll();
        return result.stream().map(r -> mapToResponse(r, "0")).toList();
//        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EventResponse> getAllEventsWithApprovedDetails() {
        List<Event> result = eventRepository.findAll();
        return result.stream().map(r -> mapToResponse(r, "1")).toList();
    }

    @Override
    public List<EventResponse> getAllUndeletedEvents() {
        List<Event> result = eventRepository.getEventByIsDeleted(false);
        return result.stream().map(r -> mapToResponse(r, "0")).toList();
//        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EventResponse> getEventByCustomerId(String id) {
        List<Event> result = eventRepository.getEventByCustomer_IdAndIsDeleted(id, false);
        return result.stream().map(r -> mapToResponse(r, "0")).toList();
//        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EventResponse> getEventByCustomerIdWithApprovedDetails(String id) {
        List<Event> result = eventRepository.getEventByCustomer_IdAndIsDeleted(id, false);
        return result.stream().map(r -> mapToResponse(r, "1")).toList();
    }

    @Override
    public EventResponse getEventById(String id) {
        Event result = findByIdOrThrowNotFound(id);
        return mapToResponse(result, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EventResponse editEvent(String id, EventRequest request) {
        try {
            Event event = findByIdOrThrowNotFound(id);
            event.setName(request.getName());
            event.setDescription(request.getDescription());
            event.setStartDate(request.getStartDate());
            event.setStartTime(request.getStartTime());
            event.setEndDate(request.getEndDate());
            event.setEndTime(request.getEndTime());
            event.setProvince(request.getProvince());
            event.setCity(request.getCity());
            event.setDistrict(request.getDistrict());
            event.setAddress(request.getAddress());
            event.setTheme(request.getTheme());
            event.setParticipant(request.getParticipant());
            event.setModifiedDate(LocalDateTime.now());
            eventRepository.saveAndFlush(event);
            return mapToResponse(event, "0");
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteEvent(String id) {
        try {
            Event result = findByIdOrThrowNotFound(id);
            result.setIsDeleted(true);
            result.setModifiedDate(LocalDateTime.now());
            eventRepository.saveAndFlush(result);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Event findByIdOrThrowNotFound(String id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "event not found"));
    }

    private EventResponse mapToResponse(Event event, String statusChoice) {
        List<EventDetailResponse> eventDetailResponseList;
        if (statusChoice.equals("1")) {
            eventDetailResponseList = eventDetailService.getEventDetailByEventIdAndApproved(event.getId());
        } else {
            eventDetailResponseList = eventDetailService.getEventDetailByEventIdAndAllApprovalStatus(event.getId());
        }
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .endDate(event.getEndDate())
                .endTime(event.getEndTime())
                .province(event.getProvince())
                .city(event.getCity())
                .district(event.getDistrict())
                .address(event.getAddress())
                .theme(event.getTheme())
                .participant(event.getParticipant())
                .customerId(event.getCustomer().getId())
                .customerName(event.getCustomer().getFullName())
                .eventDetailResponseList(eventDetailResponseList)
                .isDeleted(event.getIsDeleted())
                .createdDate(event.getCreatedDate())
                .modifiedDate(event.getModifiedDate())
                .build();
    }
}
