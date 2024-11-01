package com.eska.evenity.controller;

import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.ProductService;
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
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllVendor() {
        try {
            List<VendorResponse> vendorResponses = vendorService.getAllVendor();
            WebResponse<List<VendorResponse>> response = WebResponse.<List<VendorResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveVendor() {
        try {
            List<VendorResponse> vendorResponses = vendorService.getAllActiveVendor();
            WebResponse<List<VendorResponse>> response = WebResponse.<List<VendorResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable String id) {
        try {
            VendorResponse vendorResponse = vendorService.getVendorById(id);
            WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<?> getVendorAndProducts(@PathVariable String id) {
        try {
            VendorWithProductsResponse vendorWithProductsResponse = productService.getProductsByVendorId(id);
            WebResponse<VendorWithProductsResponse> response = WebResponse.<VendorWithProductsResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorWithProductsResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editVendor(@PathVariable String id, @Valid @RequestBody VendorRequest request) {
        try {
            VendorResponse vendorResponse = vendorService.updateVendor(id, request);
            WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVendorStatus(@PathVariable String id) {
        try {
            VendorResponse vendorResponse = vendorService.approveStatusVendor(id);
            WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully update data")
                    .data(vendorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectVendorStatus(@PathVariable String id) {
        try {
            VendorResponse vendorResponse = vendorService.rejectStatusVendor(id);
            WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully update data")
                    .data(vendorResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        try {
            vendorService.softDeleteById(id);
            WebResponse<VendorResponse> response = WebResponse.<VendorResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("User deleted")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
