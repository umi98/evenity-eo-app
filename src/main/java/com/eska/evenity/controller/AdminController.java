package com.eska.evenity.controller;

import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.response.AdminDashboardSummary;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.RegisterResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.AdminDashboardService;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminController {
    private final CustomerService customerService;
    private final AdminDashboardService adminDashboardService;
    private final VendorService vendorService;
    private final TransactionService transactionService;

    /*
     * Disable Customer using customer id
     */
    @PutMapping("/customer/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableCustomer(@PathVariable String id) {
        try {
            CustomerResponse customerResponse = customerService.disableCustomer(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Status change success")
                    .data(customerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /*
     * Enable customer using customer id
     */
    @PutMapping("/customer/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableCustomer(@PathVariable String id) {
        try {
            CustomerResponse customerResponse = customerService.enableCustomer(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Status change success")
                    .data(customerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /*
     * Admin Dashboard Summary
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> dashboardSummary() {
        try {
            AdminDashboardSummary summary = adminDashboardService.getSummary();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(summary)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
