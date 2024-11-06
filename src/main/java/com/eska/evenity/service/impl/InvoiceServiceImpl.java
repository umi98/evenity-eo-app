package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.PaymentDetailRequest;
import com.eska.evenity.dto.request.PaymentRequest;
import com.eska.evenity.dto.response.InvoiceDetailResponse;
import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.entity.*;
import com.eska.evenity.repository.InvoiceDetailRepository;
import com.eska.evenity.repository.InvoiceRepository;
import com.eska.evenity.repository.PaymentRepository;
import com.eska.evenity.service.InvoiceService;
import com.eska.evenity.service.TransactionService;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.error.MidtransError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final TransactionService transactionService;
    private final MidTransServiceImpl midTransService;
    private final PaymentServiceImpl paymentService;

    @Value("${midtrans.snap.url}")
    private String snapUrl;

    @Override
    public Page<InvoiceResponse> getInvoices(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return invoices.map(this::mapToResponse);
    }

    @Override
    public InvoiceResponse getInvoiceByIdInResponse(String id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "invoice not found"));
        return mapToResponse(invoice);
    }

    @Override
    public Page<InvoiceResponse> getInvoicesByCustomerId(String id, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Invoice> invoice = invoiceRepository.findByEvent_Customer_Id(id, pageable);
        return invoice.map(this::mapToResponse);
    }

    @Override
    public Page<InvoiceResponse> getInvoiceDetailByVendorId(String id, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<InvoiceDetail> invoiceDetailPage = invoiceDetailRepository.findByEventDetail_Product_Vendor_Id(id, pageable);

        List<InvoiceResponse> invoiceList = invoiceDetailPage.stream().map(invoiceDetail -> {
            InvoiceDetailResponse invoiceDetailResponse = InvoiceDetailResponse.builder()
                    .invoiceDetailId(invoiceDetail.getId())
                    .forwardPaymentStatus(invoiceDetail.getStatus().name())
                    .productId(invoiceDetail.getEventDetail().getProduct().getId())
                    .productName(invoiceDetail.getEventDetail().getProduct().getName())
                    .qty(invoiceDetail.getEventDetail().getQuantity())
                    .unit(invoiceDetail.getEventDetail().getUnit().name())
                    .cost(invoiceDetail.getEventDetail().getCost())
                    .build();
            Invoice invoice = invoiceRepository.findById(invoiceDetail.getInvoice().getId()).get();
            return InvoiceResponse.builder()
                    .invoiceId(invoice.getId())
                    .startDate(invoice.getEvent().getStartDate())
                    .startTime(invoice.getEvent().getStartTime())
                    .endDate(invoice.getEvent().getEndDate())
                    .endTime(invoice.getEvent().getEndTime())
                    .eventId(invoice.getEvent().getId())
                    .eventName(invoice.getEvent().getName())
                    .theme(invoice.getEvent().getTheme())
                    .province(invoice.getEvent().getProvince())
                    .city(invoice.getEvent().getCity())
                    .district(invoice.getEvent().getDistrict())
                    .address(invoice.getEvent().getAddress())
                    .participant(invoice.getEvent().getParticipant())
                    .customerId(invoice.getEvent().getCustomer().getId())
                    .customerName(invoice.getEvent().getCustomer().getFullName())
                    .phoneNumber(invoice.getEvent().getCustomer().getPhoneNumber())
                    .customerProvince(invoice.getEvent().getCustomer().getProvince())
                    .customerCity(invoice.getEvent().getCustomer().getCity())
                    .customerDistrict(invoice.getEvent().getCustomer().getDistrict())
                    .customerAddress(invoice.getEvent().getCustomer().getAddress())
                    .paymentStatus(invoice.getStatus().name())
                    .paymentDate(invoice.getPaymentDate())
                    .invoiceDetailResponseList(List.of(invoiceDetailResponse))
                    .build();
        }).collect(Collectors.toList());

        // Return the list as a Page
        return new PageImpl<>(invoiceList, pageable, invoiceDetailPage.getTotalElements());
    }

    @Override
    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "invoice not found"
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createInvoice(Event event) {
        try {
            Invoice newInvoice = Invoice.builder()
                    .event(event)
                    .status(PaymentStatus.UNPAID)
                    .paymentDate(null)
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .build();
            invoiceRepository.saveAndFlush(newInvoice);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment changeStatusWhenPaid(String id) {
//        try {
            Invoice result = invoiceRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "invoice not found"
                    ));

            if (result.getStatus() == PaymentStatus.COMPLETE)
                return null;

            List<Long> costs = invoiceDetailRepository.findAllCostsByInvoiceId(id);
            Long totalCost = costs.stream().filter(Objects::nonNull).mapToLong(Long::longValue).sum();
            result.setStatus(PaymentStatus.COMPLETE);
            result.setPaymentDate(LocalDateTime.now());
            result.setModifiedDate(LocalDateTime.now());
            invoiceRepository.saveAndFlush(result);

            Payment transactionToken = paymentService.create(result.getId(), totalCost);
            transactionService.changeBalanceWhenCustomerPay(totalCost, result.getEvent());
            return transactionToken;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createInvoiceDetail(EventDetail eventDetail) {
        try {

            Invoice invoice = invoiceRepository.findByEventId(eventDetail.getEvent().getId());
            InvoiceDetail newInvoiceDetail = InvoiceDetail.builder()
                    .invoice(invoice)
                    .status(PaymentStatus.UNPAID)
                    .eventDetail(eventDetail)
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .build();
            invoiceDetailRepository.save(newInvoiceDetail);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String changeStatusWhenVendorWasPaid(String id) {
        try {
            InvoiceDetail result = invoiceDetailRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "invoice detail not found"
                    ));
            if (result.getEventDetail().getApprovalStatus() != ApprovalStatus.APPROVED)
                return "Vendor or Product was not included in event";
            if (result.getStatus() == PaymentStatus.COMPLETE)
                return "Vendor or Product has been paid";
            result.setStatus(PaymentStatus.COMPLETE);
            result.setModifiedDate(LocalDateTime.now());
            invoiceDetailRepository.saveAndFlush(result);
            Long cost = invoiceDetailRepository.findCostFromInvoiceDetail(id);
            transactionService.changeBalanceWhenTransfer(cost, result.getEventDetail());
            return result.getId();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoice_IdAndEventDetail_ApprovalStatus(invoice.getId(), ApprovalStatus.APPROVED);
        List<InvoiceDetailResponse> invoiceDetailResponses = new ArrayList<>();
        Long totalCost = 0L;
        for (InvoiceDetail detail : invoiceDetails) {
            InvoiceDetailResponse detailResponse = InvoiceDetailResponse.builder()
                    .invoiceDetailId(detail.getId())
                    .forwardPaymentStatus(detail.getStatus().name())
                    .productId(detail.getEventDetail().getProduct().getId())
                    .productName(detail.getEventDetail().getProduct().getName())
                    .vendorId(detail.getEventDetail().getProduct().getVendor().getId())
                    .vendorName(detail.getEventDetail().getProduct().getVendor().getName())
                    .qty(detail.getEventDetail().getQuantity())
                    .unit(detail.getEventDetail().getUnit().name())
                    .cost(detail.getEventDetail().getCost())
                    .build();
            invoiceDetailResponses.add(detailResponse);
            totalCost += detail.getEventDetail().getCost();
        }
        return InvoiceResponse.builder()
                .invoiceId(invoice.getId())
                .startDate(invoice.getEvent().getStartDate())
                .startTime(invoice.getEvent().getStartTime())
                .endDate(invoice.getEvent().getEndDate())
                .endTime(invoice.getEvent().getEndTime())
                .eventId(invoice.getEvent().getId())
                .eventName(invoice.getEvent().getName())
                .customerId(invoice.getEvent().getCustomer().getId())
                .customerName(invoice.getEvent().getCustomer().getFullName())
                .phoneNumber(invoice.getEvent().getCustomer().getPhoneNumber())
                .customerProvince(invoice.getEvent().getCustomer().getProvince())
                .customerCity(invoice.getEvent().getCustomer().getCity())
                .customerDistrict(invoice.getEvent().getCustomer().getDistrict())
                .customerAddress(invoice.getEvent().getCustomer().getAddress())
                .theme(invoice.getEvent().getTheme())
                .province(invoice.getEvent().getProvince())
                .city(invoice.getEvent().getCity())
                .district(invoice.getEvent().getDistrict())
                .address(invoice.getEvent().getAddress())
                .participant(invoice.getEvent().getParticipant())
                .paymentStatus(invoice.getStatus().name())
                .paymentDate(invoice.getPaymentDate())
                .createdDate(invoice.getCreatedDate())
                .totalCost(totalCost)
                .invoiceDetailResponseList(invoiceDetailResponses)
                .build();
    }
}
