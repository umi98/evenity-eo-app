package com.eska.evenity.service;

import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.TransactionDetail;
import com.eska.evenity.entity.*;

import java.util.List;

public interface InvoiceService {
//    Invoice userPaidEvent(String id);
    List<InvoiceResponse> getInvoices();
    InvoiceResponse getInvoiceByIdInResponse(String id);
    Invoice getInvoiceById(String id);
    void createInvoice(Event event);
    Payment changeStatusWhenPaid(String id);
    void createInvoiceDetail(EventDetail eventDetail);
    String changeStatusWhenVendorWasPaid(String id);
}
