package com.eska.evenity.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.PagingResponse;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.ProductService;
import com.eska.evenity.service.VendorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllVendor(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<VendorResponse> vendorResponses = vendorService.getAllVendor(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(vendorResponses.getTotalElements())
                    .totalPage(vendorResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveVendor(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<VendorResponse> vendorResponses = vendorService.getAllActiveVendor(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(vendorResponses.getTotalElements())
                    .totalPage(vendorResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponses.getContent())
                    .pagingResponse(pagingResponse)
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

    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedCustomer(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<VendorResponse> vendorResponses = vendorService.getApprovedVendor(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(vendorResponses.getTotalElements())
                    .totalPage(vendorResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(vendorResponses.getContent())
                    .pagingResponse(pagingResponse)
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
