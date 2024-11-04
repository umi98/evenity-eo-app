package com.eska.evenity.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eska.evenity.entity.Invoice;
import com.eska.evenity.entity.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        try {
            List<InvoiceResponse> invoiceResponses = invoiceService.getInvoices();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(invoiceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String id) {
        try {
            List<InvoiceResponse> invoiceResponses = invoiceService.getInvoices();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(invoiceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PostMapping("/{id}")
//    public ResponseEntity<?> userPaid(@PathVariable String id) {
//        Invoice invoice = invoiceService.userPaidEvent(id);
//        return null;
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> userPaidForEvent(@PathVariable String id) {
        try {
            Payment invoice = invoiceService.changeStatusWhenPaid(id);
//            Pattern pattern = Pattern.compile("paid");
//            Matcher matcher = pattern.matcher(invoice);
//            if (matcher.find()) {
        if (invoice == null) {
                return ResponseEntity.badRequest().build();
            }
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Successfully change data")
                    .data(invoice)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/detail/{detailId}")
    public ResponseEntity<?> forwardMoneyForProduct(@PathVariable String detailId) {
        try {
            String invoice = invoiceService.changeStatusWhenVendorWasPaid(detailId);
            Pattern pattern = Pattern.compile("not|included|paid");
            Matcher matcher = pattern.matcher(invoice);
            if (matcher.find()) {
                return ResponseEntity.badRequest().build();
            }
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Successfully change data")
                    .data(invoice)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
