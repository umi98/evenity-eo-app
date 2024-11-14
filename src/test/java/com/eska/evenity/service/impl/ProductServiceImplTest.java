package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.ProductRequest;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.entity.Product;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.ProductRepository;
import com.eska.evenity.service.CategoryService;
import com.eska.evenity.service.VendorService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct_Successful() {
        ProductRequest productRequest = new ProductRequest("Test Product", "Test Description", 100L, "categoryId", "vendorId");
        Category category = new Category();
        category.setMainCategory(CategoryType.CATERING);
        Vendor vendor = new Vendor();
        vendor.setStatus(VendorStatus.ACTIVE);

        when(categoryService.getCategoryUsingId("categoryId")).thenReturn(category);
        when(vendorService.getVendorUsingId("vendorId")).thenReturn(vendor);
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
    }

    @Test
    void testCreateProduct_VendorNotActive_ThrowsException() {
        ProductRequest productRequest = new ProductRequest("Test Product", "Test Description", 100L, "categoryId", "vendorId");
        Vendor vendor = new Vendor();
        vendor.setStatus(VendorStatus.PENDING);

        when(vendorService.getVendorUsingId("vendorId")).thenReturn(vendor);

        assertThrows(BadRequestException.class, () -> productService.createProduct(productRequest));
    }

    @Test
    void testGetProductById_ProductFound() {
        Product product = new Product();
        product.setId("productId");
        product.setName("Test Product");

        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById("productId");

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
    }

    @Test
    void testGetProductsByCategoryId_Successful() {
        PagingRequest pagingRequest = new PagingRequest(1, 10);
        Pageable pageRequest = PageRequest.of(0, 10);
        Category category = Category.builder()
                .id("1")
                .name("Snack")
                .mainCategory(CategoryType.CATERING)
                .build();
        Product product = Product.builder()
                .id("1")
                .name("Snacks")
                .category(category)
                .build();
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByCategoryId("1", pageRequest)).thenReturn(productPage);

        Page<ProductResponse> responsePage = productService.getProductsByCategoryId("1", pagingRequest);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test Product", responsePage.getContent().get(0).getName());
    }

    @Test
    void testUpdateProduct_ProductFound() {
        ProductRequest productRequest = new ProductRequest(
                "Updated Product",
                "Updated Description",
                200L,
                "categoryId",
                "vendorId");
        Product product = new Product();
        product.setId("productId");
        product.setName("Old Product");
        Category category = new Category();

        when(productRepository.findById("productId")).thenReturn(Optional.of(product));
        when(categoryService.getCategoryUsingId("categoryId")).thenReturn(category);
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateProduct("productId", productRequest);

        assertNotNull(response);
        assertEquals("Updated Product", response.getName());
        assertEquals("Updated Description", response.getDescription());
        verify(productRepository, times(1)).saveAndFlush(product);
    }

    @Test
    void testDeleteProduct_ProductFound() {
        Product product = new Product();
        product.setId("productId");

        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        productService.deleteProduct("productId");

        verify(productRepository, times(1)).saveAndFlush(product);
        assertTrue(product.getIsDeleted());
    }
}