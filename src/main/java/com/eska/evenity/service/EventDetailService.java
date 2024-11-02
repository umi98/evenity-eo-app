package com.eska.evenity.service;

import com.eska.evenity.dto.request.BaseEventRequest;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.entity.EventDetail;

import java.util.List;

public interface EventDetailService {

    List<EventDetail> addBulk(List<EventDetail> eventDetails);
    List<EventDetail> editBulk(List<EventDetail> eventDetails);
    void deleteDetail(EventDetail eventDetail);
    EventDetail approveProductReqOnEventDetail(String detailId);
    EventDetail rejectProductReqOnEventDetail(String detailId);
}
