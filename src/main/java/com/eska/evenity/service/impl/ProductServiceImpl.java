package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.entity.Product;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.ProductRepository;
import com.eska.evenity.service.CategoryService;
import com.eska.evenity.service.ProductService;
import com.eska.evenity.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final VendorService vendorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        try {
            Category category = categoryService.getCategoryUsingId(productRequest.getCategoryId());
            Vendor vendor = vendorService.getVendorUsingId(productRequest.getVendorId());
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .qty(productRequest.getQty())
                    .productUnit(ProductUnit.valueOf(productRequest.getProductUnit()))
                    .isDeleted(false)
                    .category(category)
                    .vendor(vendor)
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .build();
            productRepository.saveAndFlush(product);
            return mapToResponse(product);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductResponse getProductById(String productId) {
        return mapToResponse(findByIdOrThrowException(productId));
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(String categoryId) {
        List<Product> result = productRepository.findByCategoryId(categoryId);
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<ProductResponse> getAllAvailableProducts() {
        List<Product> result = productRepository.findByIsDeleted(false);
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public VendorWithProductsResponse getProductsByVendorId(String vendorId) {
        Vendor vendor = vendorService.getVendorUsingId(vendorId);
        List<Product> result = productRepository.findByVendorIdAndIsDeleted(vendorId, false);
        List<ProductResponse> productResponses = result.stream().map(this::mapToResponse).toList();
        return VendorWithProductsResponse.builder()
                .id(vendor.getId())
                .email(vendor.getUserCredential().getUsername())
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .province(vendor.getProvince())
                .city(vendor.getCity())
                .district(vendor.getDistrict())
                .address(vendor.getAddress())
                .owner(vendor.getOwner())
                .scoring(vendor.getScoring())
                .status(vendor.getStatus().name())
                .createdDate(vendor.getCreatedDate())
                .modifiedDate(vendor.getModifiedDate())
                .productList(productResponses)
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> result = productRepository.findAll();
        return result.stream().map(this::mapToResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        try {
            Product product = findByIdOrThrowException(productId);
            Category category = categoryService.getCategoryUsingId(productRequest.getCategoryId());
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setQty(productRequest.getQty());
            product.setProductUnit(ProductUnit.valueOf(productRequest.getProductUnit()));
            product.setCategory(category);
            product.setModifiedDate(LocalDateTime.now());
            productRepository.saveAndFlush(product);
            return mapToResponse(product);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProduct(String productId) {
        try {
            Product product = findByIdOrThrowException(productId);
            product.setIsDeleted(true);
            product.setModifiedDate(LocalDateTime.now());
            productRepository.saveAndFlush(product);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Product findByIdOrThrowException(String id) {
        Optional<Product> result = productRepository.findById(id);
        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .qty(product.getQty())
                .productUnit(product.getProductUnit().name())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .vendorId(product.getVendor().getId())
                .vendorName(product.getVendor().getName())
                .isDeleted(product.getIsDeleted())
                .createdDate(product.getCreatedDate())
                .modifiedDate(product.getModifiedDate())
                .build();
    }
}
