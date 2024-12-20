package com.eska.evenity.service;

import com.eska.evenity.dto.response.*;
import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.EventAndGenerateProductRequest;
import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.request.PagingRequest;

import java.util.HashMap;
import java.util.List;

public interface EventService {
    EventResponse addNewEvent(EventRequest request);
    EventRecommendationResponse eventAndGenerateProduct(EventAndGenerateProductRequest request);
    EventRecommendationResponse regenerateProductOnSavedEvent(String id, EventAndGenerateProductRequest request);
    RegenerateResponse regenerateWhenRejected(String eventId);
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
    List<DiagramData> getEventCountByMonth();
    Page<EventResponse> searchEvent(String name, PagingRequest pagingRequest);
    EventResponse cancelEvent(String id);
}
