package com.eska.evenity.service;

import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.PaymentResponse;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;

public interface InvoiceService {
//    Invoice userPaidEvent(String id);
//    void generateAdminFee();
    Page<InvoiceResponse> getInvoices(PagingRequest pagingRequest);
    InvoiceResponse getInvoiceByIdInResponse(String id);
    Page<InvoiceResponse> getInvoicesByCustomerId(String id, PagingRequest pagingRequest);
    Page<InvoiceResponse> getInvoiceDetailByVendorId(String id, PagingRequest pagingRequest);
    Invoice getInvoiceById(String id);
    Invoice getInvoiceByEventId(String id);
    PaymentResponse paidForEvent(String id);
    void createInvoice(Event event);
    void editAdminFee(Invoice invoice, Long nominal);
    void changeStatusWhenPaid(String orderId, String grossAmount);
    void createInvoiceDetail(EventDetail eventDetail);
    String changeStatusWhenVendorWasPaid(String id);
    void createAdminFeeInvoice(Invoice invoice, Long nominal);
    Long grossIncomeInMonth();
    Long grossIncomeAllTime();
    Page<InvoiceResponse> searchInvoice(String name, PagingRequest pagingRequest);
}
