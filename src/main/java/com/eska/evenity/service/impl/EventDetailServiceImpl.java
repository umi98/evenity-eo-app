package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.dto.request.EventDetailRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.EventDetailResponse;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Product;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.service.EventDetailService;
import com.eska.evenity.service.InvoiceService;
import com.eska.evenity.service.ProductService;
import com.eska.evenity.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventDetailServiceImpl implements EventDetailService {
    private final EventDetailRepository repository;
    private final ProductService productService;
    private final InvoiceService invoiceService;
    private final VendorService vendorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addBulk(List<EventDetailRequest> eventDetails, Event event) {
        try {
            List<EventDetail> newDetails = new ArrayList<>();
            for (EventDetailRequest request : eventDetails) {
                Product product = productService.getProductUsingId(request.getProductId());
                EventDetail newDetail = EventDetail.builder()
                        .event(event)
                        .product(product)
                        .approvalStatus(ApprovalStatus.PENDING)
                        .cost(request.getCost())
                        .eventProgress(EventProgress.NOT_STARTED)
                        .notes(request.getNotes())
                        .quantity(request.getQty())
                        .unit(ProductUnit.valueOf(request.getUnit()))
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build();
                newDetails.add(newDetail);
            }
            repository.saveAllAndFlush(newDetails);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<EventDetail> editBulk(List<EventDetailRequest> eventDetails) {
        return List.of();
    }

    @Override
    public Page<EventDetailResponse> getAllEventDetails(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<EventDetail> result = repository.findAll(pageable);
        return result.map(this::mapToResponse);
    }

    @Override
    public Page<EventDetailResponse> getEventDetailByVendorId(String vendorId, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<EventDetail> result = repository.findByProduct_Vendor_Id(vendorId, pageable);
        return result.map(this::mapToResponse);
    }

    @Override
    public List<EventDetailResponse> getEventDetailByEventIdAndApproved(String eventId) {
        List<EventDetail> result = repository.findByEventIdAndApprovalStatus(eventId, ApprovalStatus.APPROVED);
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EventDetailResponse> getEventDetailByEventIdAndAllApprovalStatus(String eventId) {
        List<EventDetail> result = repository.findByEventId(eventId);
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public EventDetailResponse getEvenDetailById(String id) {
        EventDetail result = findByIdOrThrowException(id);
        return mapToResponse(result);
    }

    @Override
    public void deleteDetail(EventDetail eventDetail) {

    }

    @Override
    public EventDetailResponse approveProductReqOnEventDetail(String detailId) {
        EventDetail result = findByIdOrThrowException(detailId);
        result.setApprovalStatus(ApprovalStatus.APPROVED);
        result.setModifiedDate(LocalDateTime.now());
        repository.saveAndFlush(result);
        invoiceService.createInvoiceDetail(result);
        vendorService.upVoteVendor(result.getProduct().getVendor().getId());
        return mapToResponse(result);
    }

    @Override
    public EventDetailResponse rejectProductReqOnEventDetail(String detailId) {
        EventDetail result = findByIdOrThrowException(detailId);
        result.setApprovalStatus(ApprovalStatus.REJECTED);
        result.setModifiedDate(LocalDateTime.now());
        repository.saveAndFlush(result);
        vendorService.downVoteVendor(result.getProduct().getVendor().getId());
        return mapToResponse(result);
    }

    private EventDetail findByIdOrThrowException(String id) {
        Optional<EventDetail> result = repository.findById(id);
        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "event detail not found"));
    }

    private EventDetailResponse mapToResponse(EventDetail eventDetail) {
        return EventDetailResponse.builder()
                .eventDetailId(eventDetail.getId())
                .approvalStatus(eventDetail.getApprovalStatus().name())
                .eventProgress(eventDetail.getEventProgress().name())
                .quantity(eventDetail.getQuantity())
                .unit(eventDetail.getUnit().name())
                .notes(eventDetail.getNotes())
                .cost(eventDetail.getCost())
                .eventId(eventDetail.getEvent().getId())
                .eventName(eventDetail.getEvent().getName())
                .productId(eventDetail.getProduct().getId())
                .productName(eventDetail.getProduct().getName())
                .vendorId(eventDetail.getProduct().getVendor().getId())
                .vendorName(eventDetail.getProduct().getVendor().getName())
                .createdDate(eventDetail.getCreatedDate())
                .modifiedDate(eventDetail.getModifiedDate())
                .build();
    }
}
