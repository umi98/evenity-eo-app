package com.eska.evenity.service;

import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.EventDetailCustomizedRequest;
import com.eska.evenity.dto.request.EventInfoRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.PriceRangeRequest;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.entity.Product;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(String productId);
    Product getProductUsingId(String productId);
    Page<ProductResponse> getProductsByCategoryId(String categoryId, PagingRequest pagingRequest);
    Page<ProductResponse> getAllAvailableProducts(PagingRequest pagingRequest);
    VendorWithProductsResponse getProductsByVendorId(String vendorId);
    void deleteProduct(String productId);
    Page<ProductResponse> getAllProducts(PagingRequest pagingRequest);
    ProductResponse updateProduct(String productId, ProductRequest productRequest);
    MinMaxPriceResponse findMaxMinPrice(PriceRangeRequest request);
    ProductRecommendedResponse getProductRecommendation(EventInfoRequest request);
    ProductRecommendedResponse generateRecommendation(EventDetailCustomizedRequest request);
}
