package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.EventDetailRequest;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;

public interface EventDetailService {
    void addBulk(List<EventDetailRequest> eventDetails, Event event);
    List<EventDetail> editBulk(List<EventDetailRequest> eventDetails);
    List<EventDetailResponse> getAllEventDetails();
    List<EventDetailResponse> getEventDetailByVendorId(String vendorId);
    List<EventDetailResponse> getEventDetailByEventIdAndApproved(String eventId);
    List<EventDetailResponse> getEventDetailByEventIdAndAllApprovalStatus(String eventId);
    EventDetailResponse getEvenDetailById(String id);
    void deleteDetail(EventDetail eventDetail);
    EventDetailResponse approveProductReqOnEventDetail(String detailId);
    EventDetailResponse rejectProductReqOnEventDetail(String detailId);
}
