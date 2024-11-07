package com.eska.evenity.controller;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.EventAndGenerateProductRequest;
import com.eska.evenity.dto.request.EventRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.dto.response.EventRecommendationResponse;
import com.eska.evenity.dto.response.EventResponse;
import com.eska.evenity.dto.response.PagingResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.EventDetailService;
import com.eska.evenity.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventDetailService eventDetailService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventResponse> eventResponses = eventService.getAllEvents(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventResponses.getTotalElements())
                    .totalPage(eventResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEventsWithApprovedDetails(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventResponse> eventResponses = eventService.getAllEventsWithApprovedDetails(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventResponses.getTotalElements())
                    .totalPage(eventResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/undeleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUndeletedEvents(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventResponse> eventResponses = eventService.getAllUndeletedEvents(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventResponses.getTotalElements())
                    .totalPage(eventResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/details")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEventDetail(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventDetailResponse> eventDetailResponseList = eventDetailService.getAllEventDetails(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventDetailResponseList.getTotalElements())
                    .totalPage(eventDetailResponseList.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventDetailResponseList.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/vendor/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventDetailByVendor(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventDetailResponse> eventDetailResponseList = eventDetailService.getEventDetailByVendorId(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventDetailResponseList.getTotalElements())
                    .totalPage(eventDetailResponseList.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventDetailResponseList.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventDetailById(@PathVariable String id) {
        try {
            EventDetailResponse eventDetailResponse = eventDetailService.getEvenDetailById(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventDetailResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/customer/{id}")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAllEventsByCustomerId(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventResponse> eventResponses = eventService.getEventByCustomerId(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventResponses.getTotalElements())
                    .totalPage(eventResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/customer/{id}/approved")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getAllEventsByCustomerIdAndApprovedDetails(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<EventResponse> eventResponses = eventService.getEventByCustomerIdWithApprovedDetails(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(eventResponses.getTotalElements())
                    .totalPage(eventResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponses.getContent())
                    .pagingResponse(pagingResponse)
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

    @PostMapping("/generate")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addEventWithGeneratedProduct(@Valid @RequestBody EventAndGenerateProductRequest request) {
        try {
            EventRecommendationResponse eventResponse = eventService.eventAndGenerateProduct(request);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(eventResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{id}/regenerate/recommendation")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addOtherProduct(@PathVariable String id, @Valid @RequestBody EventAndGenerateProductRequest request) {
        try {
            EventRecommendationResponse eventResponse = eventService.regenerateProductOnSavedEvent(id, request);
            WebResponse<?> response = WebResponse.builder()
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

    @PostMapping("/{id}/regenerate")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> editAndRegenerate(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        try {
            EventResponse eventResponse = eventService.editEventWithRegeneratedProduct(id, request);
            WebResponse<EventResponse> response = WebResponse.<EventResponse>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Successfully edit event")
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

    @PutMapping("/{id}/start")
    public ResponseEntity<?> changeEventToStarted(@PathVariable String id) {
        try {
            eventService.startEvent(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully edit data")
                    .data("Event and vendor will start working")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/detail/{id}/approve")
    public ResponseEntity<?> approveProductOnEventDetail(@PathVariable String id) {
        try {
            EventDetailResponse eventDetailResponse = eventDetailService.approveProductReqOnEventDetail(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully edit data")
                    .data(eventDetailResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/detail/{id}/reject")
    public ResponseEntity<?> rejectProductOnEventDetail(@PathVariable String id) {
        try {
            EventDetailResponse eventDetailResponse = eventDetailService.rejectProductReqOnEventDetail(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully edit data")
                    .data(eventDetailResponse)
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
