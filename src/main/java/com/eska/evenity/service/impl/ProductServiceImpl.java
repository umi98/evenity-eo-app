package com.eska.evenity.service.impl;

import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.entity.Product;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.CategoryRepository;
import com.eska.evenity.repository.ProductRepository;
import com.eska.evenity.repository.VendorRepository;
import com.eska.evenity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .productId(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .qty(productRequest.getQty())
                .productUnit(productRequest.getProductUnit())
                .build();

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Vendor vendor = vendorRepository.findById(productRequest.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        product.setCategory(category);
        product.setVendor(vendor);

        product = productRepository.save(product);
        return new ProductResponse(product.getProductId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getQty(), product.getProductUnit(), category.getName(), vendor.getName());
    }

    @Override
    public Optional<ProductResponse> getProductById(String productId) {
        return productRepository.findById(productId)
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQty(),
                        product.getProductUnit(),
                        product.getCategory().getName(),
                        product.getVendor().getName()));
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQty(),
                        product.getProductUnit(),
                        product.getCategory().getName(),
                        product.getVendor().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByVendorId(String vendorId) {
        return productRepository.findByVendorId(vendorId).stream()
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQty(),
                        product.getProductUnit(),
                        product.getCategory().getName(),
                        product.getVendor().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQty(),
                        product.getProductUnit(),
                        product.getCategory().getName(),
                        product.getVendor().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> updateProduct(String productId, ProductRequest productRequest) {
        return productRepository.findById(productId).map(product -> {
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setQty(productRequest.getQty());
            product.setProductUnit(productRequest.getProductUnit());

            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            Vendor vendor = vendorRepository.findById(productRequest.getVendorId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            product.setCategory(category);
            product.setVendor(vendor);

            product = productRepository.save(product);
            return new ProductResponse(product.getProductId(), product.getName(), product.getDescription(),
                    product.getPrice(), product.getQty(), product.getProductUnit(), category.getName(), vendor.getName());
        });
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }
}
