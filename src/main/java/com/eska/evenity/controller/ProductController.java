package com.eska.evenity.controller;

import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<WebResponse<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                .status(HttpStatus.CREATED.getReasonPhrase())
                .message("Product successfully created")
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<WebResponse<ProductResponse>> getProductById(@PathVariable String productId) {
        return productService.getProductById(productId)
                .map(productResponse -> {
                    WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                            .status(HttpStatus.OK.getReasonPhrase())
                            .message("Successfully retrieved product")
                            .data(productResponse)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(WebResponse.<ProductResponse>builder()
                                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message("Product not found")
                                .build()));
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieved all products")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<WebResponse<List<ProductResponse>>> getProductsByCategoryId(@PathVariable String categoryId) {
        List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
        WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieved products by category")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<WebResponse<List<ProductResponse>>> getProductsByVendorId(@PathVariable String vendorId) {
        List<ProductResponse> products = productService.getProductsByVendorId(vendorId);
        WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieved products by vendor")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<WebResponse<ProductResponse>> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(productId, productRequest)
                .map(updatedProduct -> {
                    WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                            .status(HttpStatus.OK.getReasonPhrase())
                            .message("Product successfully updated")
                            .data(updatedProduct)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(WebResponse.<ProductResponse>builder()
                                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message("Product not found")
                                .build()));
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<WebResponse<Void>> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        WebResponse<Void> response = WebResponse.<Void>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Product successfully deleted.")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

}
