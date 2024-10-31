package com.eska.evenity.service;

import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    Optional<ProductResponse> getProductById(String productId);
    List<ProductResponse> getProductsByCategoryId(String categoryId);
    List<ProductResponse> getProductsByVendorId(String vendorId);
    void deleteProduct(String productId);
    List<ProductResponse> getAllProducts();
    Optional<ProductResponse> updateProduct(String productId, ProductRequest productRequest);
}
