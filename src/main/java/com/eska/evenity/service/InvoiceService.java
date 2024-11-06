package com.eska.evenity.service;

import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.InvoiceDetailResponse;
import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.PagingResponse;
import com.eska.evenity.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InvoiceService {
//    Invoice userPaidEvent(String id);
    Page<InvoiceResponse> getInvoices(PagingRequest pagingRequest);
    InvoiceResponse getInvoiceByIdInResponse(String id);
    Page<InvoiceResponse> getInvoicesByCustomerId(String id, PagingRequest pagingRequest);
    Page<InvoiceResponse> getInvoiceDetailByVendorId(String id, PagingRequest pagingRequest);
    Invoice getInvoiceById(String id);
    Payment paidForEvent(String id);
    void createInvoice(Event event);
    void changeStatusWhenPaid(String orderId);
    void createInvoiceDetail(EventDetail eventDetail);
    String changeStatusWhenVendorWasPaid(String id);
}
