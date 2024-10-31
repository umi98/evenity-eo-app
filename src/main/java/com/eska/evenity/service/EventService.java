package com.eska.evenity.service;

import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse addNewEvent(String customerId, EventRequest request);
    List<EventResponse> getAllEvents();
    List<EventResponse> getAllUndeletedEvents();
    List<EventResponse> getEventByCustomerId(String id);
    EventResponse getEventById(String id);
    EventResponse editEvent(String id, EventRequest request);
    void deleteEvent(String id);
}
