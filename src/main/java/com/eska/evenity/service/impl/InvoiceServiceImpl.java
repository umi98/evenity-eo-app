package com.eska.evenity.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.AdminFeeResponse;
import com.eska.evenity.dto.response.InvoiceDetailResponse;
import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.PaymentResponse;
import com.eska.evenity.entity.AdminFee;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;
import com.eska.evenity.entity.InvoiceDetail;
import com.eska.evenity.entity.Payment;
import com.eska.evenity.repository.AdminFeeRepository;
import com.eska.evenity.repository.InvoiceDetailRepository;
import com.eska.evenity.repository.InvoiceRepository;
import com.eska.evenity.service.InvoiceService;
import com.eska.evenity.service.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final TransactionService transactionService;
    private final AdminFeeRepository adminFeeRepository;
    private final PaymentServiceImpl paymentService;

    @Value("${midtrans.snap.url}")
    private String snapUrl;

//    @Override
//    public void generateAdminFee() {
//        List<Invoice> invoices = invoiceRepository.findAll();
//        for (Invoice invoice : invoices) {
//            createAdminFeeInvoice(invoice, 0L);
//        }
//    }

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
                    .categoryId(invoiceDetail.getEventDetail().getProduct().getCategory().getId())
                    .categoryName(invoiceDetail.getEventDetail().getProduct().getCategory().getName())
                    .qty(invoiceDetail.getEventDetail().getQuantity())
                    .unit(invoiceDetail.getEventDetail().getUnit().name())
                    .cost(invoiceDetail.getEventDetail().getCost())
                    .build();
            Invoice invoice = invoiceRepository.findById(invoiceDetail.getInvoice().getId()).get();
            AdminFee adminFee = adminFeeRepository.findByInvoice_Id(invoice.getId()).orElse(null);
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
                    .adminFeeResponse(AdminFeeResponse.builder()
                            .adminFeeId(adminFee.getId())
                            .eventId(adminFee.getInvoice().getEvent().getId())
                            .eventName(adminFee.getInvoice().getEvent().getName())
                            .customerId(adminFee.getInvoice().getEvent().getCustomer().getId())
                            .customerId(adminFee.getInvoice().getEvent().getCustomer().getFullName())
                            .paymentStatus(adminFee.getStatus().name())
                            .cost(adminFee.getNominal())
                            .build())
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

    @Override
    public Invoice getInvoiceByEventId(String id) {
        return invoiceRepository.findByEventId(id);
    }

    @Override
    public PaymentResponse paidForEvent(String id) {
        Invoice result = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "invoice not found"
                ));
        if (result.getStatus() == PaymentStatus.COMPLETE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This invoice has been paid");
        List<Long> costs = invoiceDetailRepository.findAllCostsByInvoiceId(id);
        Long totalCost = costs.stream().filter(Objects::nonNull).mapToLong(Long::longValue).sum();
        AdminFee adminFee = adminFeeRepository.findByInvoice_Id(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "admin fee not found"));
        totalCost += adminFee.getNominal();
        return paymentService.create(result, totalCost);
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
            createAdminFeeInvoice(newInvoice, 0L);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void editAdminFee(Invoice invoice, Long nominal) {
        Optional<AdminFee> adminFeeOpt = adminFeeRepository.findByInvoice_Id(invoice.getId());
        if (adminFeeOpt.isPresent()) {
            AdminFee adminFee = adminFeeOpt.get();
            adminFee.setNominal(nominal);
            adminFee.setModifiedDate(LocalDateTime.now());
            adminFeeRepository.saveAndFlush(adminFee);
        } else {
            AdminFee adminFee = AdminFee.builder()
                    .invoice(invoice)
                    .status(PaymentStatus.UNPAID)
                    .nominal(nominal)
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .build();
            adminFeeRepository.saveAndFlush(adminFee);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeStatusWhenPaid(String orderId, String grossAmount) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            Invoice result = getInvoiceById(payment.getInvoice().getId());
            AdminFee adminFee = adminFeeRepository.findByInvoice_Id(result.getId()).get();
            List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoice_Id(result.getId());

            result.setStatus(PaymentStatus.COMPLETE);
            result.setPaymentDate(LocalDateTime.now());
            result.setModifiedDate(LocalDateTime.now());
            invoiceRepository.saveAndFlush(result);

            adminFee.setStatus(PaymentStatus.COMPLETE);
            adminFee.setModifiedDate(LocalDateTime.now());
            adminFeeRepository.saveAndFlush(adminFee);

            Long accPartialInvoiceDetail = 0L;
            for (InvoiceDetail invoiceDetail : invoiceDetails) {
                Long cost = (long) (invoiceDetail.getEventDetail().getCost() * 0.5);
                accPartialInvoiceDetail += cost;
                invoiceDetail.setStatus(PaymentStatus.PARTIAL);
                invoiceDetail.setModifiedDate(LocalDateTime.now());
                invoiceDetailRepository.save(invoiceDetail);
                transactionService.changeBalanceWhenTransfer(cost, invoiceDetail.getEventDetail());
            }

            Long totalCost = (long) Double.parseDouble(grossAmount) - accPartialInvoiceDetail;
            transactionService.changeBalanceWhenCustomerPay(totalCost, result.getEvent());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
            if (result.getInvoice().getStatus() == PaymentStatus.UNPAID)
                return "User has not paid for event";
            result.setStatus(PaymentStatus.COMPLETE);
            result.setModifiedDate(LocalDateTime.now());
            invoiceDetailRepository.saveAndFlush(result);
            Long cost = (long) (invoiceDetailRepository.findCostFromInvoiceDetail(id) * 0.5);
            transactionService.changeBalanceWhenTransfer(cost, result.getEventDetail());
            return result.getId();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void createAdminFeeInvoice(Invoice invoice, Long nominal) {
        AdminFee adminFee = AdminFee.builder()
                .invoice(invoice)
                .status(PaymentStatus.UNPAID)
                .nominal(nominal)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        adminFeeRepository.saveAndFlush(adminFee);
    }

    @Override
    public Long grossIncomeInMonth() {
        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay();

        return invoiceRepository.calculateGrossIncomeForMonth(PaymentStatus.COMPLETE, startOfMonth, endOfMonth);
    }

    @Override
    public Long grossIncomeAllTime() {
        return invoiceRepository.calculateAllTimeGrossIncome(PaymentStatus.COMPLETE);
    }

    @Override
    public Page<InvoiceResponse> searchInvoice(String name, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Invoice> result = invoiceRepository.findByCustomerNameOrEventName(name, pageable);
        return result.map(this::mapToResponse);
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
                    .categoryId(detail.getEventDetail().getProduct().getCategory().getId())
                    .categoryName(detail.getEventDetail().getProduct().getCategory().getName())
                    .vendorId(detail.getEventDetail().getProduct().getVendor().getId())
                    .vendorName(detail.getEventDetail().getProduct().getVendor().getName())
                    .qty(detail.getEventDetail().getQuantity())
                    .unit(detail.getEventDetail().getUnit().name())
                    .cost(detail.getEventDetail().getCost())
                    .build();
            invoiceDetailResponses.add(detailResponse);
            totalCost += detail.getEventDetail().getCost();
        }
        AdminFee adminFee = adminFeeRepository.findByInvoice_Id(invoice.getId()).orElse(null);
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
                .adminFeeResponse(AdminFeeResponse.builder()
                        .adminFeeId(adminFee.getId())
                        .eventId(adminFee.getInvoice().getEvent().getId())
                        .eventName(adminFee.getInvoice().getEvent().getName())
                        .customerId(adminFee.getInvoice().getEvent().getCustomer().getId())
                        .customerName(adminFee.getInvoice().getEvent().getCustomer().getFullName())
                        .paymentStatus(adminFee.getStatus().name())
                        .cost(adminFee.getNominal())
                        .build())
                .build();
    }
}
