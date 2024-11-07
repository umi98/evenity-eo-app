package com.eska.evenity.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.InvoiceResponse;
import com.eska.evenity.dto.response.PagingResponse;
import com.eska.evenity.dto.response.PaymentResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<?> getAllInvoices(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<InvoiceResponse> invoiceResponses = invoiceService.getInvoices(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(invoiceResponses.getTotalElements())
                    .totalPage(invoiceResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(invoiceResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String id) {
        try {
            InvoiceResponse invoiceResponses = invoiceService.getInvoiceByIdInResponse(id);
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

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getInvoiceByCustomerId(
            @PathVariable String id,
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<InvoiceResponse> invoiceResponses = invoiceService.getInvoicesByCustomerId(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(invoiceResponses.getTotalElements())
                    .totalPage(invoiceResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(invoiceResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/vendor/{id}")
    public ResponseEntity<?> getInvoiceByVendorId(
            @PathVariable String id,
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<InvoiceResponse> invoiceResponses = invoiceService.getInvoiceDetailByVendorId(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(invoiceResponses.getTotalElements())
                    .totalPage(invoiceResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(invoiceResponses.getContent())
                    .pagingResponse(pagingResponse)
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
            PaymentResponse invoice = invoiceService.paidForEvent(id);
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
