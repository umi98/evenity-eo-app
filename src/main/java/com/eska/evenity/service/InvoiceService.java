package com.eska.evenity.service;

import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.TransactionDetail;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;
import com.eska.evenity.entity.InvoiceDetail;

import java.util.List;

public interface InvoiceService {
    List<InvoiceResponse> getInvoices();
    Invoice getInvoiceById(String id);
    void createInvoice(Event event);
    String changeStatusWhenPaid(String id);
    void createInvoiceDetail(EventDetail eventDetail);
    String changeStatusWhenVendorWasPaid(String id);
}
