package com.eska.evenity.service;

import com.eska.evenity.dto.request.EventAndGenerateProductRequest;
import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.response.EventRecommendationResponse;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.TransactionDetail;

import java.util.List;

public interface EventService {
    EventResponse addNewEvent(EventRequest request);
    EventRecommendationResponse eventAndGenerateProduct(EventAndGenerateProductRequest request);
    EventRecommendationResponse regenerateProductOnSavedEvent(String id, EventAndGenerateProductRequest request);
    TransactionDetail getTransactionByInvoiceId(String invoiceId);
    List<EventResponse> getAllEvents();
    List<EventResponse> getAllEventsWithApprovedDetails();
    List<EventResponse> getAllUndeletedEvents();
    List<EventResponse> getEventByCustomerId(String id);
    List<EventResponse> getEventByCustomerIdWithApprovedDetails(String id);
    EventResponse getEventById(String id);
    EventResponse editEvent(String id, EventRequest request);
    EventResponse editEventWithRegeneratedProduct(String id, EventRequest request);
    void deleteEvent(String id);
}
