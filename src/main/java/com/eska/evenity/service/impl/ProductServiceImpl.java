package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.EventDetailCustomizedRequest;
import com.eska.evenity.dto.request.EventInfoRequest;
import com.eska.evenity.dto.request.PriceRangeRequest;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
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
            if (vendor.getStatus() != VendorStatus.ACTIVE) throw new BadRequestException("Vendor is yet to approved");
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
    public Product getProductUsingId(String productId) {
        return findByIdOrThrowException(productId);
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
                .mainCategory(vendor.getMainCategory().name())
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

    @Override
    public MinMaxPriceResponse findMaxMinPrice(PriceRangeRequest request) {
        try {
            Long min = productRepository.findMinPrice(request.getCategoryId(), request.getProvince(), request.getCity());
            Long max = productRepository.findMaxPrice(request.getCategoryId(), request.getProvince(), request.getCity());
            CategoryType mainCategory = categoryService.getCategoryUsingId(request.getCategoryId()).getMainCategory();
            Long calculatedDate = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            if (min == null || max == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No product found");
            }
            if (mainCategory == CategoryType.CATERING) {
                min *= request.getParticipant();
                max *= request.getParticipant();
            } else {
                min *= calculatedDate;
                max *= calculatedDate;
            }
            System.out.println(request.getCategoryId());
            return MinMaxPriceResponse.builder()
                    .highestPrice(max)
                    .lowestPrice(min)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductRecommendedResponse getProductRecommendation(EventInfoRequest request) {
        try {
            Long minCost = request.getMinCost();
            Long maxCost = request.getMaxCost();
            Category category = categoryService.getCategoryUsingId(request.getCategoryId());
            if (category.getMainCategory() == CategoryType.CATERING) {
                minCost = minCost / request.getParticipant();
                maxCost = maxCost / request.getParticipant();
            }
            List<Product> products = productRepository.findRecommendation(
                    request.getProvince(),
                    request.getCity(),
                    request.getCategoryId(),
                    minCost,
                    maxCost,
                    request.getPreviousProduct()
            );
            System.out.println(minCost);
            System.out.println(maxCost);
            System.out.println(request.getProvince());
            System.out.println(request.getCity());
            System.out.println(request.getCategoryId());
            System.out.println(request.getPreviousProduct());
            if (products.isEmpty()) {
                return null;
            }
            products.sort(Comparator.comparingInt((Product p) -> p.getVendor().getScoring())
                    .reversed()
                    .thenComparingLong(Product::getPrice)
                    .reversed()
                    .thenComparing(p -> Math.random()));
            Product chosenProduct = products.get(0);
            Long cost = chosenProduct.getPrice();
            if (category.getMainCategory() == CategoryType.CATERING) cost *= request.getParticipant();
            return ProductRecommendedResponse.builder()
                    .vendorId(chosenProduct.getVendor().getId())
                    .vendorName(chosenProduct.getVendor().getName())
                    .vendorAddress(chosenProduct.getVendor().getAddress())
                    .productId(chosenProduct.getId())
                    .productName(chosenProduct.getName())
                    .productDescription(chosenProduct.getDescription())
                    .cost(cost)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductRecommendedResponse generateRecommendation(
            EventDetailCustomizedRequest request
    ) {
        try {
            Long minCost = request.getMinCost();
            Long maxCost = request.getMaxCost();
            Category category = categoryService.getCategoryUsingId(request.getCategoryId());
            System.out.println(category.getMainCategory());
            if (category.getMainCategory() == CategoryType.CATERING) {
                minCost = minCost / request.getParticipant();
                maxCost = maxCost / request.getParticipant();
            } else {
                minCost = minCost / request.getDuration();
                maxCost = maxCost / request.getDuration();
            }
            System.out.println(minCost);
            System.out.println(maxCost);
            System.out.println(request.getProvince());
            System.out.println(request.getCity());
            System.out.println(request.getCategoryId());
            System.out.println(request.getPreviousList());
            List<Product> products = productRepository.findRecommendation(
                    request.getProvince(),
                    request.getCity(),
                    request.getCategoryId(),
                    minCost,
                    maxCost,
                    request.getPreviousList()
            );
            if (products.isEmpty()) {
                return null;
            }
            products.sort(Comparator.comparingInt((Product p) -> p.getVendor().getScoring())
                    .reversed()
                    .thenComparingLong(Product::getPrice)
                    .reversed()
                    .thenComparing(p -> Math.random()));
            Product chosenProduct = products.get(0);
            System.out.println(chosenProduct.getName());
            Long cost = chosenProduct.getPrice();
            if (category.getMainCategory() == CategoryType.CATERING) cost *= request.getParticipant();
            else cost *= request.getDuration();
            return ProductRecommendedResponse.builder()
                    .vendorId(chosenProduct.getVendor().getId())
                    .vendorName(chosenProduct.getVendor().getName())
                    .vendorAddress(chosenProduct.getVendor().getAddress())
                    .productId(chosenProduct.getId())
                    .productName(chosenProduct.getName())
                    .productDescription(chosenProduct.getDescription())
                    .cost(cost)
                    .build();
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
                .id(product.getId())
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
