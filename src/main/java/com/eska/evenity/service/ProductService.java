package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.BaseEventRequest;
import com.eska.evenity.dto.request.EventInfoRequest;
import com.eska.evenity.dto.request.PriceRangeRequest;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(String productId);
    List<ProductResponse> getProductsByCategoryId(String categoryId);
    List<ProductResponse> getAllAvailableProducts();
    VendorWithProductsResponse getProductsByVendorId(String vendorId);
    void deleteProduct(String productId);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(String productId, ProductRequest productRequest);
    MinMaxPriceResponse findMaxMinPrice(PriceRangeRequest request);
    ProductRecommendedResponse getProductRecommendation(EventInfoRequest request);
}
