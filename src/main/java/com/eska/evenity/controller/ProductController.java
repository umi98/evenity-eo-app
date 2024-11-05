package com.eska.evenity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eska.evenity.dto.request.EventInfoRequest;
import com.eska.evenity.dto.request.PriceRangeRequest;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        try {
            ProductResponse productResponse = productService.createProduct(productRequest);
            WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Product successfully created")
                    .data(productResponse)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        try {
            ProductResponse productResponse = productService.getProductById(productId);
            WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(productResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieved all products")
                    .data(products)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableProducts() {
        try {
            List<ProductResponse> products = productService.getAllAvailableProducts();
            WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieved all products")
                    .data(products)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable String categoryId) {
        try {
            List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
            WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieved products by category")
                    .data(products)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/price/range")
    public ResponseEntity<?> minMaxPrice(@Valid @RequestBody PriceRangeRequest request) {
        try {
            MinMaxPriceResponse products = productService.findMaxMinPrice(request);
            WebResponse<MinMaxPriceResponse> response = WebResponse.<MinMaxPriceResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieved data")
                    .data(products)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/event/recommend")
    public ResponseEntity<?> getSingleRecommendationFromCategory(@Valid @RequestBody EventInfoRequest request) {
        try {
            ProductRecommendedResponse products = productService.getProductRecommendation(request);
            if (products == null) {
                return ResponseEntity.notFound().build();
            }
            WebResponse<ProductRecommendedResponse> response = WebResponse.<ProductRecommendedResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieved data")
                    .data(products)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @GetMapping("/vendor/{vendorId}")
//    public ResponseEntity<?> getProductsByVendorId(@PathVariable String vendorId) {
//        try {
//            List<ProductResponse> products = productService.getProductsByVendorId(vendorId);
//            WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
//                    .status(HttpStatus.OK.getReasonPhrase())
//                    .message("Successfully retrieved products by vendor")
//                    .data(products)
//                    .build();
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest productRequest) {
        try {
            ProductResponse productResponse = productService.updateProduct(productId, productRequest);
            WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Product successfully updated")
                    .data(productResponse)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        try {
            productService.deleteProduct(productId);
            WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Product successfully deleted.")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
