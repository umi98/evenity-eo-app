package com.eska.evenity.controller;

import com.eska.evenity.dto.request.CustomerRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.security.JwtUtils;
import com.eska.evenity.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCustomer() {
        try {
            List<CustomerResponse> customerResponses = customerService.getAllCustomer();
            WebResponse<List<CustomerResponse>> response = WebResponse.<List<CustomerResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(customerResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/active")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveCustomer() {
        try {
            List<CustomerResponse> customerResponses = customerService.getAllActiveCustomer();
            WebResponse<List<CustomerResponse>> response = WebResponse.<List<CustomerResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(customerResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        try {
            CustomerResponse customerResponse = customerService.getCustomerById(id);
            WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(customerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editCustomer(@PathVariable String id, @Valid @RequestBody CustomerRequest request) {
        try {
            CustomerResponse customerResponse = customerService.editCustomer(id, request);
            WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(customerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("User deleted")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    private boolean checkUserId(HttpServletRequest httpRequest, String id) {
//        String token = httpRequest.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//
//        String userIdFromToken = jwtUtils.getUserInfoByToken(token).getUserId();
//        System.out.println(userIdFromToken);
//        String customerId = customerService.getCustomerByUserId(id).getId();
//        return userIdFromToken != null && userIdFromToken.equals(customerId);
//    }
}
