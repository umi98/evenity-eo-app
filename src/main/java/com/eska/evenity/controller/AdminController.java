package com.eska.evenity.controller;

import com.eska.evenity.dto.response.*;
import com.eska.evenity.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final CustomerService customerService;
    private final AdminDashboardService adminDashboardService;
    private final EventService eventService;
    private final UserService userService;

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

    /*
     * Get Data for Diagram : Event
     */
    @GetMapping("/admin/diagram/event")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> diagramDataEvent() {
        try {
            List<DiagramData> diagramData = eventService.getEventCountByMonth();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(diagramData)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /*
     * Get Data for Diagram : User
     */
    @GetMapping("/admin/diagram/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> diagramDataUser() {
        try {
            List<DiagramData> diagramData = userService.getEventCountByMonth();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully fetch data")
                    .data(diagramData)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
