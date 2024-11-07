package com.eska.evenity.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.MidtransNotification;
import com.eska.evenity.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MidtransNotificationController {
    private final InvoiceService invoiceService;

    @Value("${midtrans.server.key}")
    private String SECRET_KEY;

    @PostMapping("/api/midtrans/notification")
    public ResponseEntity<String> handleNotification(@RequestBody MidtransNotification notification) {
        System.out.println(notification.getTransaction_status());
        if (notification.getTransaction_status().equals("settlement")) {
            try {
                System.out.println(notification.getTransaction_status());
                invoiceService.changeStatusWhenPaid(notification.getOrder_id(), notification.getGross_amount());
                return new ResponseEntity<>("Invoice updated successfully", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Invoice not found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Notification received", HttpStatus.OK);
    }
}
