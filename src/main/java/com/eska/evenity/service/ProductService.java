package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.*;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.entity.Product;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(String productId);
    Product getProductUsingId(String productId);
    List<ProductResponse> getProductsByCategoryId(String categoryId);
    List<ProductResponse> getAllAvailableProducts();
    VendorWithProductsResponse getProductsByVendorId(String vendorId);
    void deleteProduct(String productId);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(String productId, ProductRequest productRequest);
    MinMaxPriceResponse findMaxMinPrice(PriceRangeRequest request);
    ProductRecommendedResponse getProductRecommendation(EventInfoRequest request);
    ProductRecommendedResponse generateRecommendation(EventDetailCustomizedRequest request);
}
