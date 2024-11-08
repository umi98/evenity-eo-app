package com.eska.evenity.service;

import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.EventAndGenerateProductRequest;
import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.EventRecommendationResponse;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.TransactionDetail;

import java.util.HashMap;

public interface EventService {
    EventResponse addNewEvent(EventRequest request);
    EventRecommendationResponse eventAndGenerateProduct(EventAndGenerateProductRequest request);
    EventRecommendationResponse regenerateProductOnSavedEvent(String id, EventAndGenerateProductRequest request);
    TransactionDetail getTransactionByInvoiceId(String invoiceId);
    Page<EventResponse> getAllEvents(PagingRequest pagingRequest);
    Page<EventResponse> getAllEventsWithApprovedDetails(PagingRequest pagingRequest);
    Page<EventResponse> getAllUndeletedEvents(PagingRequest pagingRequest);
    Page<EventResponse> getEventByCustomerId(String id, PagingRequest pagingRequest);
    Page<EventResponse> getEventByCustomerIdWithApprovedDetails(String id, PagingRequest pagingRequest);
    EventResponse getEventById(String id);
    EventResponse editEvent(String id, EventRequest request);
    EventResponse editEventWithRegeneratedProduct(String id, EventRequest request);
    Long numOfEventHeldThisMonth();
    HashMap<String, Long> numOfFuturePastEvents();
    void startEvent(String eventId);
//    PaymentResponse paidForEventProceeding(String eventId);
    void deleteEvent(String id);
}
