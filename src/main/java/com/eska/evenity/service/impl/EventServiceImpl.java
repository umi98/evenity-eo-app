package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.dto.request.EventAndGenerateProductRequest;
import com.eska.evenity.dto.request.EventDetailCustomizedRequest;
import com.eska.evenity.dto.request.EventInfoMinimalistRequest;
import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.dto.response.EventRecommendationResponse;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.TransactionDetail;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;
import com.eska.evenity.repository.EventRepository;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.EventDetailService;
import com.eska.evenity.service.EventService;
import com.eska.evenity.service.InvoiceService;
import com.eska.evenity.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CustomerService customerService;
    private final EventDetailService eventDetailService;
    private final InvoiceService invoiceService;
    private final ProductService productService;
//    private final PaymentServiceImpl paymentService;

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
    public EventRecommendationResponse eventAndGenerateProduct(EventAndGenerateProductRequest request) {
        try {
            Customer customer = customerService.getCustomerByCustomerId(request.getCustomerId());
            System.out.println(request.getPreviousProduct());
            Event event = Event.builder()
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
            Long calculatedDate = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;
            List<ProductRecommendedResponse> recommendedList = new ArrayList<>();
            for (EventInfoMinimalistRequest request1 : request.getCategoryProduct()) {
                EventDetailCustomizedRequest customizedRequest = EventDetailCustomizedRequest.builder()
                        .categoryId(request1.getCategoryId())
                        .province(event.getProvince())
                        .city(event.getCity())
                        .minCost(request1.getMinCost())
                        .maxCost(request1.getMaxCost())
                        .participant(event.getParticipant())
                        .duration(calculatedDate)
                        .previousList(request.getPreviousProduct())
                        .build();
                ProductRecommendedResponse result = productService.generateRecommendation(customizedRequest);
                recommendedList.add(result);
            }
            return EventRecommendationResponse.builder()
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
                    .recommendedList(recommendedList)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public EventRecommendationResponse regenerateProductOnSavedEvent(String id, EventAndGenerateProductRequest request) {
        try {
            findByIdOrThrowNotFound(id);
            return eventAndGenerateProduct(request);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TransactionDetail getTransactionByInvoiceId(String invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        Customer customer = customerService.getCustomerByCustomerId(invoice.getEvent().getCustomer().getId());
        CustomerResponse customerResponse = CustomerResponse.builder()
                .userId(customer.getUserCredential().getId())
                .id(customer.getId())
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
                .paymentStatus(invoice.getStatus().name())
                .build();
    }

    @Override
    public Page<EventResponse> getAllEvents(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Event> result = eventRepository.findAll(pageable);
        return result.map(r -> mapToResponse(r, "0"));
    }

    @Override
    public Page<EventResponse> getAllEventsWithApprovedDetails(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Event> result = eventRepository.findAll(pageable);
        return result.map(r -> mapToResponse(r, "1"));
    }

    @Override
    public Page<EventResponse> getAllUndeletedEvents(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Event> result = eventRepository.getEventByIsDeleted(false, pageable);
        return result.map(r -> mapToResponse(r, "0"));
    }

    @Override
    public Page<EventResponse> getEventByCustomerId(String id, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Event> result = eventRepository.getEventByCustomer_IdAndIsDeleted(id, false, pageable);
        return result.map(r -> mapToResponse(r, "0"));
    }

    @Override
    public Page<EventResponse> getEventByCustomerIdWithApprovedDetails(String id, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Event> result = eventRepository.getEventByCustomer_IdAndIsDeleted(id, false, pageable);
        return result.map(r -> mapToResponse(r, "1"));
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
    public EventResponse editEventWithRegeneratedProduct(String id, EventRequest request) {
        try {
            Event event = findByIdOrThrowNotFound(id);
            editEvent(id, request);
            eventDetailService.addBulk(request.getEventDetail(), event);
            return mapToResponse(event, "0");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void startEvent(String eventId) {
        findByIdOrThrowNotFound(eventId);
        List<EventDetail> eventDetails = eventDetailService.getEventDetailByEventIdAndApprovedRegForm(eventId);
        List<EventDetail> editBulk = new ArrayList<>();
        for (EventDetail eventDetail : eventDetails) {
            eventDetail.setEventProgress(EventProgress.ON_PROGRESS);
            eventDetail.setModifiedDate(LocalDateTime.now());
            editBulk.add(eventDetail);
        }
        eventDetailService.editBulk(editBulk);
    }
//
//    @Override
//    public PaymentResponse paidForEventProceeding(String eventId) {
//        Event event = findByIdOrThrowNotFound(eventId);
//        if (event.getIsProceeded()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This event has pre-service paid");
//        }
//        return paymentService.paidPreService(event);
//    }

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
                .phoneNumber(event.getCustomer().getPhoneNumber())
                .customerName(event.getCustomer().getFullName())
                .eventDetailResponseList(eventDetailResponseList)
                .isDeleted(event.getIsDeleted())
                .createdDate(event.getCreatedDate())
                .modifiedDate(event.getModifiedDate())
                .build();
    }
}
