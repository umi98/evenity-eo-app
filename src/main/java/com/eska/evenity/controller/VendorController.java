package com.eska.evenity.controller;

import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllVendor() {
        List<VendorResponse> vendorResponses = vendorService.getAllVendor();
        WebResponse<List<VendorResponse>> response = WebResponse.<List<VendorResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .status("Successfully retrieve data")
                .data(vendorResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable String id) {
        VendorResponse vendorResponse = vendorService.getVendorById(id);
        WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieve data")
                .data(vendorResponse)
                .build();
        return ResponseEntity.ok(response);
    }
}
