package com.eska.evenity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEvents() {
        try {
            List<EventResponse> eventResponses = eventService.getAllEvents();
            WebResponse<List<EventResponse>> response = WebResponse.<List<EventResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/undeleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUndeletedEvents() {
        try {
            List<EventResponse> eventResponses = eventService.getAllUndeletedEvents();
            WebResponse<List<EventResponse>> response = WebResponse.<List<EventResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/customer/{id}")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAllEventsByCustomerId(@PathVariable String id) {
        try {
        List<EventResponse> eventResponses = eventService.getEventByCustomerId(id);
        WebResponse<List<EventResponse>> response = WebResponse.<List<EventResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieve data")
                .data(eventResponses)
                .build();
        return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping()
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addNewEvent(@Valid @RequestBody EventRequest request) {
        try {
            EventResponse eventResponse = eventService.addNewEvent(request);
            WebResponse<EventResponse> response = WebResponse.<EventResponse>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Successfully create event")
                    .data(eventResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        try {
            EventResponse eventResponse = eventService.getEventById(id);
            WebResponse<EventResponse> response = WebResponse.<EventResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editEvent(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        try {
            EventResponse eventResponse = eventService.editEvent(id, request);
            WebResponse<EventResponse> response = WebResponse.<EventResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully edit data")
                    .data(eventResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            WebResponse<EventResponse> response = WebResponse.<EventResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully delete data")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
