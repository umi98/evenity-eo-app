package com.eska.evenity.controller;

import com.eska.evenity.dto.request.MidtransNotification;
import com.eska.evenity.service.InvoiceService;
import com.eska.evenity.service.TransactionService;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MidtransNotificationController {
    private final TransactionService transactionService;
    private final InvoiceService invoiceService;

    @Value("${midtrans.server.key}")
    private String SECRET_KEY;

    @PostMapping("/api/midtrans/notification")
    public ResponseEntity<String> handleNotification(@RequestBody MidtransNotification notification) {
        if ("settlement".equals(notification.getTransaction_status())) {
            try {
                invoiceService.changeStatusWhenPaid(notification.getOrder_id());
                return new ResponseEntity<>("Invoice updated successfully", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Invoice not found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Notification received", HttpStatus.OK);
    }
}
