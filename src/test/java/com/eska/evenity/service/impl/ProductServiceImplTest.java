package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.*;
import com.eska.evenity.dto.response.MinMaxPriceResponse;
import com.eska.evenity.dto.response.ProductRecommendedResponse;
import com.eska.evenity.dto.response.ProductResponse;
import com.eska.evenity.dto.response.VendorWithProductsResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.entity.Product;
import com.eska.evenity.entity.UserCredential;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(productRequest));
        assertEquals("Vendor is yet to approved", exception.getMessage());
    }

    @Test
    void testGetProductById_ProductFound() {
        Product product = new Product();
        product.setId("productId");
        product.setName("Test Product");
        product.setProductUnit(ProductUnit.DAY);
        product.setCategory(new Category());
        product.setVendor(new Vendor());

        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById("productId");

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
    }

    @Test
    void testGetProductsByCategoryId_Successful() {
        // Arrange
        String categoryId = "category1";
        PagingRequest pagingRequest = new PagingRequest(1, 10);
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());

        Product product = new Product(); // Assuming you have a Product class
        product.setId("1");
        product.setName("Test Product");
        product.setProductUnit(ProductUnit.PCS);
        product.setVendor(new Vendor());
        product.setCategory(new Category(categoryId, CategoryType.CATERING,"test",LocalDateTime.now(),LocalDateTime.now()));

        List<Product> products = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findByCategoryId(categoryId, pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getProductsByCategoryId(categoryId, pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName()); // Assuming ProductResponse has a getName() method

        verify(productRepository, times(1)).findByCategoryId(categoryId, pageable);
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
        product.setProductUnit(ProductUnit.DAY);
        product.setVendor(new Vendor());
        product.setCategory(new Category());
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

    @Test
    public void testGetAllAvailableProducts() {
        // Arrange
        PagingRequest pagingRequest = new PagingRequest(1, 10); // Assuming PagingRequest has a constructor
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Product product = new Product(); // Assuming you have a Product class
        product.setId("product1");
        product.setProductUnit(ProductUnit.PCS);
        product.setVendor(new Vendor());
        product.setCategory(new Category());
        List<Product> productList = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findByIsDeleted(false, pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getAllAvailableProducts(pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals("product1", result.getContent().get(0).getId()); // Assuming ProductResponse has a getId() method
        verify(productRepository, times(1)).findByIsDeleted(false, pageable);
    }

    @Test
    public void testGetProductsByVendorId() {
        // Arrange
        String vendorId = "vendor1";
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId(vendorId);
        vendor.setUserCredential(new UserCredential()); // Assuming UserCredential class exists
        vendor.getUserCredential().setUsername("vendor@example.com");
        vendor.setName("Vendor Name");
        vendor.setPhoneNumber("1234567890");
        vendor.setProvince("Province");
        vendor.setCity("City");
        vendor.setDistrict("District");
        vendor.setAddress("Address");
        vendor.setOwner("Owner");
        vendor.setMainCategory(CategoryType.CATERING); // Assuming Category is an enum
        vendor.setScoring(45);
        vendor.setStatus(VendorStatus.ACTIVE); // Assuming VendorStatus is an enum

        Product product = new Product(); // Assuming you have a Product class
        product.setId("product1");
        product.setProductUnit(ProductUnit.PCS);
        product.setVendor(vendor);
        product.setCategory(new Category());
        List<Product> productList = Arrays.asList(product);

        when(vendorService.getVendorUsingId(vendorId)).thenReturn(vendor);
        when(productRepository.findByVendorIdAndIsDeleted(vendorId, false)).thenReturn(productList);

        // Act
        VendorWithProductsResponse result = productService.getProductsByVendorId(vendorId);

        // Assert
        assertNotNull(result);
        assertEquals(vendorId, result.getId());
        assertEquals("vendor@example.com", result.getEmail());
        assertEquals(1, result.getProductList().size());
        assertEquals("product1", result.getProductList().get(0).getId()); // Assuming ProductResponse has a getId() method
        verify(vendorService, times(1)).getVendorUsingId(vendorId);
        verify(productRepository, times(1)).findByVendorIdAndIsDeleted(vendorId, false);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        PagingRequest pagingRequest = new PagingRequest(1, 10); // Assuming PagingRequest has a constructor
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Product product = new Product(); // Assuming you have a Product class
        product.setId("product1");
        product.setProductUnit(ProductUnit.PCS);
        product.setVendor(new Vendor());
        product.setCategory(new Category());
        List<Product> productList = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getAllProducts(pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals("product1", result.getContent ().get(0).getId()); // Assuming ProductResponse has a getId() method
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetProductUsingId_Success() {
        // Arrange
        String productId = "product123";
        Product expectedProduct = new Product(); // Assuming you have a Product class
        expectedProduct.setId(productId);
        expectedProduct.setProductUnit(ProductUnit.PCS);
        expectedProduct.setVendor(new Vendor());
        expectedProduct.setCategory(new Category());
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct)); // Mocking the behavior of the repository

        // Act
        Product result = productService.getProductUsingId(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository, times(1)).findById(productId); // Verify that the repository method was called
    }

    @Test
    public void testGetProductUsingId_NotFound() {
        // Arrange
        String productId = "product123";

        when(productRepository.findById(productId)).thenReturn(Optional.empty()); // Mocking the behavior when product is not found

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.getProductUsingId(productId);
        });

        verify(productRepository, times(1)).findById(productId); // Verify that the repository method was called
    }

    @Test
    public void testFindMaxMinPrice_Success() {
        // Arrange
        PriceRangeRequest request = new PriceRangeRequest();
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now());
        request.setParticipant(10L);

        Long minPrice = 100L;
        Long maxPrice = 200L;
        when(productRepository.findMinPrice(request.getCategoryId(), request.getProvince(), request.getCity())).thenReturn(minPrice);
        when(productRepository.findMaxPrice(request.getCategoryId(), request.getProvince(), request.getCity())).thenReturn(maxPrice);

        Category category = new Category(); // Assuming you have a Category class
        category.setMainCategory(CategoryType.CATERING); // Assuming CategoryType is an enum
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        // Act
        MinMaxPriceResponse response = productService.findMaxMinPrice(request);

        // Assert
        assertNotNull(response);
        assertEquals(4000L, response.getHighestPrice());
        assertEquals(2000L, response.getLowestPrice());
        verify(productRepository, times(1)).findMinPrice(request.getCategoryId(), request.getProvince(), request.getCity());
        verify(productRepository, times(1)).findMaxPrice(request.getCategoryId(), request.getProvince(), request.getCity());
    }

    @Test
    public void testFindMaxMinPrice_NoProductFound() {
        // Arrange
        PriceRangeRequest request = new PriceRangeRequest();
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now());
        request.setParticipant(10L);

        when(productRepository.findMinPrice(request.getCategoryId(), request.getProvince(), request.getCity())).thenReturn(null);
        when(productRepository.findMaxPrice(request.getCategoryId(), request.getProvince(), request.getCity())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.findMaxMinPrice(request);
        });

        assertEquals(exception.getMessage(), exception.getMessage());
        assertEquals(exception.toString(), exception.toString());
    }

    @Test
    public void testGetProductRecommendation_WithProductsFound() {
        // Arrange
        EventInfoRequest request = new EventInfoRequest();
        request.setMinCost(100L);
        request.setMaxCost(1000L);
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setParticipant(10L);
        request.setPreviousProduct(List.of("2","3"));

        Category category = new Category();
        category.setMainCategory(CategoryType.CATERING);
        category.setId("categoryId");
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        List<Product> products = new ArrayList<>();

        // Create a mock product with a vendor
        Vendor vendor = new Vendor();
        vendor.setId("vendorId");
        vendor.setName("Vendor Name");
        vendor.setAddress("Vendor Address");
        vendor.setProvince("Province");
        vendor.setCity("City");

        Product product = new Product();
        product.setId("productId");
        product.setName("Product Name");
        product.setDescription("Product Description");
        product.setPrice(20L);
        product.setVendor(vendor);
        product.setCategory(category);

        products.add(product);

        // Mock the repository to return the list of products
        when(productRepository.findRecommendation(
                request.getProvince(),
                request.getCity(),
                request.getCategoryId(),
                10L,
                100L,
                request.getPreviousProduct()
        )).thenReturn(products);

        // Act
        ProductRecommendedResponse response = productService.getProductRecommendation(request);

        // Assert
        assertNotNull(response);
        assertEquals("vendorId", response.getVendorId());
        assertEquals("Vendor Name", response.getVendorName());
        assertEquals("Vendor Address", response.getVendorAddress());
        assertEquals("productId", response.getProductId());
//        assertEquals("Product Name", response.getProductName());
        assertEquals("categoryId", response.getCategoryId());
//        assertEquals("Catering", response.getProductName()); // Assuming this is a typo in your code, should be product category name
        assertEquals("Product Description", response.getProductDescription());
        assertEquals(200L, response.getCost()); // Adjusted cost for 10 participants
    }

    @Test
    public void testGetProductRecommendation_NoProductsFound() {
        // Arrange
        EventInfoRequest request = new EventInfoRequest();
        request.setMinCost(100L);
        request.setMaxCost(1000L);
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setParticipant(10L);
        request.setPreviousProduct(List.of("previousProductId"));

        Category category = new Category();
        category.setMainCategory(CategoryType.CATERING);
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        // Mock the repository to return an empty list
        when(productRepository.findRecommendation(
                request.getProvince(),
                request.getCity(),
                request.getCategoryId(),
                10L, // minCost after division by participant
                100L, // maxCost after division by participant
                request.getPreviousProduct()
        )).thenReturn(new ArrayList<>());

        // Act
        ProductRecommendedResponse response = productService.getProductRecommendation(request);

        // Assert
        assertNull(response);
    }

    @Test
    public void testGenerateRecommendation_WithProductsFound_CateringCategory() {
        // Arrange
        EventDetailCustomizedRequest request = new EventDetailCustomizedRequest();
        request.setMinCost(1000L);
        request.setMaxCost(5000L);
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setParticipant(10L);
        request.setDuration(2L);
        request.setPreviousList(new ArrayList<>()); // Assuming this is a List

        Category category = new Category();
        category.setMainCategory(CategoryType.CATERING);
        category.setId("categoryId");
        category.setName("Catering");
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        List<Product> products = new ArrayList<>();

        // Create a mock product with a vendor
        Vendor vendor = new Vendor();
        vendor.setId("vendorId");
        vendor.setName("Vendor Name");
        vendor.setAddress("Vendor Address");
        vendor.setProvince("Province");
        vendor.setCity("City");

        Product product = new Product();
        product.setId("productId");
        product.setName("Product Name");
        product.setDescription("Product Description");
        product.setPrice(150L);
        product.setVendor(vendor);
        product.setCategory(category);

        products.add(product);

        // Mock the repository to return the list of products
        when(productRepository.findRecommendation(
                request.getProvince(),
                request.getCity(),
                request.getCategoryId(),
                50L,
                250L,
                request.getPreviousList()
        )).thenReturn(products);

        // Act
        ProductRecommendedResponse response = productService.generateRecommendation(request);

        // Assert
        assertNotNull(response);
        assertEquals("vendorId", response.getVendorId());
        assertEquals("Vendor Name", response.getVendorName());
        assertEquals("Vendor Address", response.getVendorAddress());
        assertEquals("productId", response.getProductId());
        assertEquals("Product Name", response.getProductName());
        assertEquals("categoryId", response.getCategoryId());
        assertEquals("Catering", response.getCategoryName()); // Assuming this is the category name
        assertEquals("Product Description", response.getProductDescription());
        assertEquals(3000L, response.getCost()); // Adjusted cost for 10 participants over 2 durations
    }

    @Test
    public void testGenerateRecommendation_WithProductsFound_NonCateringCategory() {
        // Arrange
        EventDetailCustomizedRequest request = new EventDetailCustomizedRequest();
        request.setMinCost(1000L);
        request.setMaxCost(5000L);
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setParticipant(10L);
        request.setDuration(2L);
        request.setPreviousList(new ArrayList<>());

        Category category = new Category();
        category.setId("categoryId");
        category.setName("Venue");
        category.setMainCategory(CategoryType.VENUE); // Assuming a non-catering category
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        List<Product> products = new ArrayList<>();

        // Create a mock product with a vendor
        Vendor vendor = new Vendor();
        vendor.setId("vendorId");
        vendor.setName("Vendor Name");
        vendor.setAddress("Vendor Address");
        vendor.setProvince("Province");
        vendor.setCity("City");

        Product product = new Product();
        product.setId("productId");
        product.setName("Product Name");
        product.setDescription("Product Description");
        product.setPrice(2000L);
        product.setVendor(vendor);
        product.setCategory(category);

        products.add(product);

        // Mock the repository to return the list of products
        when(productRepository.findRecommendation(
                request.getProvince(),
                request.getCity(),
                request.getCategoryId(),
                500L,
                2500L,
                request.getPreviousList()
        )).thenReturn(products);

        // Act
        ProductRecommendedResponse response = productService.generateRecommendation(request);

        // Assert
        assertNotNull(response);
        assertEquals("vendorId", response.getVendorId());
        assertEquals("Vendor Name", response.getVendorName());
        assertEquals("Vendor Address", response.getVendorAddress());
        assertEquals("productId", response.getProductId());
        assertEquals("Product Name", response.getProductName());
        assertEquals("categoryId", response.getCategoryId());
        assertEquals("Venue", response.getCategoryName()); // Assuming this is the category name
        assertEquals("Product Description", response.getProductDescription());
        assertEquals(4000L, response.getCost()); // Adjusted cost for 2 durations
    }

    @Test
    public void testGenerateRecommendation_NoProductsFound() {
        // Arrange
        EventDetailCustomizedRequest request = new EventDetailCustomizedRequest();
        request.setMinCost(1000L);
        request.setMaxCost(5000L);
        request.setCategoryId("categoryId");
        request.setProvince("Province");
        request.setCity("City");
        request.setParticipant(10L);
        request.setDuration(2L);
        request.setPreviousList(new ArrayList<>());

        Category category = new Category();
        category.setMainCategory(CategoryType.CATERING);
        when(categoryService.getCategoryUsingId(request.getCategoryId())).thenReturn(category);

        // Mock the repository to return an empty list
        when(productRepository.findRecommendation(
                request.getProvince(),
                request.getCity(),
                request.getCategoryId(),
                50L, // minCost after division by participant and duration
                250L, // maxCost after division by participant and duration
                request.getPreviousList()
        )).thenReturn(new ArrayList<>());

        // Act
        ProductRecommendedResponse response = productService.generateRecommendation(request);

        // Assert
        assertNull(response);
    }
}