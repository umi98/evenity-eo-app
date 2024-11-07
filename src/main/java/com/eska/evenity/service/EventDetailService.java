package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.EventDetailRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import org.springframework.data.domain.Page;

public interface EventDetailService {
    void addBulk(List<EventDetailRequest> eventDetails, Event event);
    void editBulk(List<EventDetail> eventDetails);
    Page<EventDetailResponse> getAllEventDetails(PagingRequest pagingRequest);
    Page<EventDetailResponse> getEventDetailByVendorId(String vendorId, PagingRequest pagingRequest);
    List<EventDetailResponse> getEventDetailByEventIdAndApproved(String eventId);
    List<EventDetail> getEventDetailByEventIdAndApprovedRegForm(String eventId);
    List<EventDetailResponse> getEventDetailByEventIdAndAllApprovalStatus(String eventId);
    EventDetailResponse getEvenDetailById(String id);
    void deleteDetail(EventDetail eventDetail);
    EventDetailResponse approveProductReqOnEventDetail(String detailId);
    EventDetailResponse rejectProductReqOnEventDetail(String detailId);
}
