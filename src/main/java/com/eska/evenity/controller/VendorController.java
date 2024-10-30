package com.eska.evenity.controller;

import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
                .message("Successfully retrieve data")
                .data(vendorResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveVendor() {
        List<VendorResponse> vendorResponses = vendorService.getAllActiveVendor();
        WebResponse<List<VendorResponse>> response = WebResponse.<List<VendorResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieve data")
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

    @PutMapping("/{id}")
    public ResponseEntity<?> editVendor(@PathVariable String id, @Valid @RequestBody VendorRequest request) {
        VendorResponse vendorResponse = vendorService.updateVendor(id, request);
        WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieve data")
                .data(vendorResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVendorStatus(@PathVariable String id) {
        VendorResponse vendorResponse = vendorService.approveStatusVendor(id);
        WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully update data")
                .data(vendorResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectVendorStatus(@PathVariable String id) {
        VendorResponse vendorResponse = vendorService.rejectStatusVendor(id);
        WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully update data")
                .data(vendorResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        vendorService.softDeleteById(id);
        WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("User deleted")
                .build();
        return ResponseEntity.ok(response);
    }
}
