package com.eska.evenity.service.impl;

import com.eska.evenity.constant.*;
import com.eska.evenity.dto.JwtClaim;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.*;
import com.eska.evenity.entity.*;
import com.eska.evenity.repository.UserCredentialRepository;
import com.eska.evenity.security.JwtUtils;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.RoleService;
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private RoleService roleService;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private VendorService vendorService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCustomerRegister_Success() {
        // Arrange
        CustomerRegisterRequest request = new CustomerRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("John Doe");
        request.setProvince("Province");
        request.setCity("City");
        request.setDistrict("District");
        request.setAddress("Address");
        request.setPhoneNumber("123456789");

        Role roleCustomer = new Role();
        roleCustomer.setRole(ERole.ROLE_CUSTOMER);
        when(roleService.getOrSave(ERole.ROLE_CUSTOMER)).thenReturn(roleCustomer);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedPassword");
        userCredential.setRole(roleCustomer);
        userCredential.setStatus(UserStatus.ACTIVE);
        userCredential.setCreatedDate(LocalDateTime.now());
        userCredential.setModifiedDate(LocalDateTime.now());

        when(userCredentialRepository.saveAndFlush(any(UserCredential.class))).thenReturn(userCredential);

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        when(customerService.createCustomer(any(Customer.class), eq(userCredential))).thenReturn(customer);

        // Act
        RegisterResponse response = authService.customerRegister(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
    }

    @Test
    public void testVendorRegister_Success() {
        // Arrange
        VendorRegisterRequest request = new VendorRegisterRequest();
        request.setEmail("vendor@example.com");
        request.setPassword("password");
        request.setName("Vendor Name");
        request.setProvince("Province");
        request.setCity("City");
        request.setDistrict("District");
        request.setAddress("Address");
        request.setPhoneNumber("987654321");
        request.setOwnerName("Owner Name");
        request.setMainCategory("CATERING");

        Role roleVendor = new Role();
        roleVendor.setRole(ERole.ROLE_VENDOR);
        when(roleService.getOrSave(ERole.ROLE_VENDOR)).thenReturn(roleVendor);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedVendorPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedVendorPassword");
        userCredential.setRole(roleVendor);
        userCredential.setStatus(UserStatus.ACTIVE);
        userCredential.setCreatedDate(LocalDateTime.now());
        userCredential.setModifiedDate(LocalDateTime.now());

        when(userCredentialRepository.saveAndFlush(any(UserCredential.class))).thenReturn(userCredential);

        Vendor vendor = new Vendor();
        vendor.setName(request.getName());
        when(vendorService.createVendor(any(Vendor.class), eq(userCredential))).thenReturn(vendor);

        // Act
        RegisterResponse response = authService.vendorRegister(request);

        // Assert
        assertNotNull(response);
        assertEquals("vendor@example.com", response.getEmail());
        assertEquals("Vendor Name", response.getName());
    }

    @Test
    public void testLogin_Vendor_Success() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("vendor@example.com");
        request.setPassword("vendorPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setId("vendorId");
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedVendorPassword"); // Mocked hashed password
        userCredential.setStatus(UserStatus.ACTIVE);
        Role role = new Role();
        role.setRole(ERole.ROLE_VENDOR);
        userCredential.setRole(role);

        Vendor vendor = new Vendor();
        vendor.setStatus(VendorStatus.ACTIVE); // Vendor is active

        // Mocking password matching to return true
        when(passwordEncoder.matches(request.getPassword(), userCredential.getPassword())).thenReturn(true);

        // Mocking authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userCredential, null));

        // Mocking vendorService to return an active vendor
        when(vendorService.getVendorByUserId(userCredential.getId())).thenReturn(vendor);

        // Mocking JWT token generation
        String expectedToken = "vendorToken"; // Mocked token
        when(jwtUtils.generateToken(userCredential)).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
    }

    @Test
    public void testLogin_Vendor_Disabled() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("vendor@example.com");
        request.setPassword("vendorPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setId("vendorId");
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedVendorPassword");
        userCredential.setStatus(UserStatus.ACTIVE);
        Role role = new Role();
        role.setRole(ERole.ROLE_VENDOR);
        userCredential.setRole(role);

        Vendor vendor = new Vendor();
        vendor.setStatus(VendorStatus.DISABLED); // Vendor is disabled

        // Mocking password matching to return true
        when(passwordEncoder.matches(request.getPassword(), userCredential.getPassword())).thenReturn(true);

        // Mocking authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userCredential, null));

        // Mocking vendorService to return a disabled vendor
        when(vendorService.getVendorByUserId(userCredential.getId())).thenReturn(vendor);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Login failed because user is disabled", response.getMessage());
        assertNull(response.getToken()); // Ensure token is null for disabled vendors
    }

    @Test
    public void testLogin_Customer_Success() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("customer@example.com");
        request.setPassword("customerPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setId("customerId");
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedCustomerPassword");
        userCredential.setStatus(UserStatus.ACTIVE);
        Role role = new Role();
        role.setRole(ERole.ROLE_CUSTOMER);
        userCredential.setRole(role);

        Customer customer = new Customer();
        customer.setStatus(CustomerStatus.ACTIVE); // Customer is active

        // Mocking password matching to return true
        when(passwordEncoder.matches(request.getPassword(), userCredential.getPassword())).thenReturn(true);

        // Mocking authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userCredential, null));

        // Mocking customerService to return an active customer
        when(customerService.getCustomerByUserId(userCredential.getId())).thenReturn(customer);

        // Mocking JWT token generation
        String expectedToken = "customerToken"; // Mocked token
        when(jwtUtils.generateToken(userCredential)).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService .login(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
    }

    @Test
    public void testLogin_Customer_Disabled() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("customer@example.com");
        request.setPassword("customerPassword");

        UserCredential userCredential = new UserCredential();
        userCredential.setId("customerId");
        userCredential.setUsername(request.getEmail());
        userCredential.setPassword("hashedCustomerPassword");
        userCredential.setStatus(UserStatus.ACTIVE);
        Role role = new Role();
        role.setRole(ERole.ROLE_CUSTOMER);
        userCredential.setRole(role);

        Customer customer = new Customer();
        customer.setStatus(CustomerStatus.DISABLED); // Customer is disabled

        // Mocking password matching to return true
        when(passwordEncoder.matches(request.getPassword(), userCredential.getPassword())).thenReturn(true);

        // Mocking authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userCredential, null));

        // Mocking customerService to return a disabled customer
        when(customerService.getCustomerByUserId(userCredential.getId())).thenReturn(customer);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Login failed because user is disabled", response.getMessage());
        assertNull(response.getToken()); // Ensure token is null for disabled customers
    }

    @Test
    public void testLogin_InvalidCredentials() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("invalid@example.com");
        request.setPassword("wrongPassword");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Invalid username and password", response.getMessage());
        assertNull(response.getToken()); // Ensure token is null for invalid credentials
    }

    @Test
    public void testGetUserInfoUsingToken_Success() {
        // Arrange
        String token = "validToken";
        JwtClaim claim = new JwtClaim();
        claim.setUserId("userId");

        UserCredential userCredential = new UserCredential();
        userCredential.setId("userId");
        userCredential.setUsername("user@example.com");
        userCredential.setRole(new Role("1", ERole.ROLE_CUSTOMER, LocalDateTime.now(), LocalDateTime.now()));

        Customer customer = new Customer();
        customer.setFullName("John Doe");
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setUserCredential(userCredential);
        when(jwtUtils.getUserInfoByToken(token)).thenReturn(claim);
        when(userCredentialRepository.findById(claim.getUserId())).thenReturn(Optional.of(userCredential));
        when(customerService.getCustomerByUserId(userCredential.getId())).thenReturn(customer);

        // Act
        ProfileResponse<?> response = authService.getUserInfoUsingToken(token);

        // Assert
        assertNotNull(response);
        assertEquals("user@example.com", response.getEmail());
        assertEquals("John Doe", ((CustomerResponse) response.getDetail()).getFullName());
    }

    @Test
    public void testGetUserInfoUsingToken_UserNotFound() {
        // Arrange
        String token = "invalidToken";
        JwtClaim claim = new JwtClaim();
        claim.setUserId("userId");

        when(jwtUtils.getUserInfoByToken(token)).thenReturn(claim);
        when(userCredentialRepository.findById(claim.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> authService.getUserInfoUsingToken(token));
    }

    @Test
    void testGetUserInfoFromSearch_ReturnsPaginatedResults() {
        PagingRequest pagingRequest = PagingRequest.builder()
                .page(1)
                .size(10)
                .build();
        // Setup mock customer and vendor data
        Role roleCustomer = new Role("role01", ERole.ROLE_CUSTOMER, LocalDateTime.now(),LocalDateTime.now());
        Role roleVendor = new Role("role02", ERole.ROLE_VENDOR, LocalDateTime.now(),LocalDateTime.now());
        UserCredential userCustomer = UserCredential.builder()
                .id("user01")
                .status(UserStatus.ACTIVE)
                .role(roleCustomer)
                .build();
        Customer customer = new Customer();
        customer.setId("customerId");
        customer.setUserCredential(userCustomer);
        customer.setStatus(CustomerStatus.ACTIVE);

        UserCredential userVendor = UserCredential.builder()
                .id("user02")
                .status(UserStatus.ACTIVE)
                .role(roleVendor)
                .build();
        Vendor vendor = new Vendor();
        vendor.setId("vendorId");
        vendor.setUserCredential(userVendor);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setMainCategory(CategoryType.ENTERTAINER);

        when(customerService.searchCustomer("testName")).thenReturn(List.of(customer));
        when(vendorService.searchVendor("testName")).thenReturn(List.of(vendor));
        when(userCredentialRepository.findById("user01")).thenReturn(Optional.of(userCustomer));
        when(userCredentialRepository.findById("user02")).thenReturn(Optional.of(userVendor));

        // Invoke method
        Page<ProfileResponse<?>> result = authService.getUserInfoFromSearch("testName", pagingRequest);

        // Verify the response
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());

        // Verify interactions
        verify(customerService, times(1)).searchCustomer("testName");
        verify(vendorService, times(1)).searchVendor("testName");
        verify(userCredentialRepository, times(1)).findById("user01");
        verify(userCredentialRepository, times(1)).findById("user02");
    }

    @Test
    void testGetUserInfoFromSearch_NoUserCredentialFound() {
        PagingRequest pagingRequest = PagingRequest.builder()
                .page(1)
                .size(10)
                .build();
        // Setup mock data with no UserCredential
        Role roleCustomer = new Role("role01", ERole.ROLE_CUSTOMER, LocalDateTime.now(),LocalDateTime.now());
        Role roleVendor = new Role("role02", ERole.ROLE_VENDOR, LocalDateTime.now(),LocalDateTime.now());
        UserCredential userCustomer = UserCredential.builder()
                .id("user01")
                .status(UserStatus.ACTIVE)
                .role(roleCustomer)
                .build();
        Customer customer = new Customer();
        customer.setId("customerId");
        customer.setUserCredential(userCustomer);
        customer.setStatus(CustomerStatus.ACTIVE);

        UserCredential userVendor = UserCredential.builder()
                .id("user02")
                .status(UserStatus.ACTIVE)
                .role(roleVendor)
                .build();
        Vendor vendor = new Vendor();
        vendor.setId("vendorId");
        vendor.setUserCredential(userVendor);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setMainCategory(CategoryType.ENTERTAINER);

        when(customerService.searchCustomer("testName")).thenReturn(List.of(customer));
        when(vendorService.searchVendor("testName")).thenReturn(List.of(vendor));
        when(userCredentialRepository.findById("customerCredentialId")).thenReturn(Optional.empty());
        when(userCredentialRepository.findById("vendorCredentialId")).thenReturn(Optional.empty());

        // Invoke method
        Page<ProfileResponse<?>> result = authService.getUserInfoFromSearch("testName", pagingRequest);

        // Verify the response
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        // Verify interactions
        verify(customerService, times(1)).searchCustomer("testName");
        verify(vendorService, times(1)).searchVendor("testName");
        verify(userCredentialRepository, times(1)).findById("user01");
        verify(userCredentialRepository, times(1)).findById("user02");
    }

    @Test
    void testGetUserInfoFromSearch_Pagination() {
        PagingRequest pagingRequest = PagingRequest.builder()
                .page(1)
                .size(10)
                .build();
        // Setup mock data for pagination check
        Role roleCustomer = new Role("role01", ERole.ROLE_CUSTOMER, LocalDateTime.now(),LocalDateTime.now());
        UserCredential userCredential1 = new UserCredential();
        userCredential1.setId("userCredentialId1");
        userCredential1.setRole(roleCustomer);
        UserCredential userCredential2 = new UserCredential();
        userCredential2.setId("userCredentialId2");
        userCredential2.setRole(roleCustomer);
        Customer customer1 = new Customer();
        customer1.setId("customerId1");
        customer1.setUserCredential(userCredential1);
        customer1.setStatus(CustomerStatus.ACTIVE);
        Customer customer2 = new Customer();
        customer2.setId("customerId2");
        customer2.setUserCredential(userCredential2);
        customer2.setStatus(CustomerStatus.ACTIVE);


        when(customerService.searchCustomer("testName")).thenReturn(Arrays.asList(customer1, customer2));
        when(userCredentialRepository.findById("userCredentialId1")).thenReturn(Optional.of(userCredential1));
        when(userCredentialRepository.findById("userCredentialId2")).thenReturn(Optional.of(userCredential2));

        // Adjust pagingRequest to return 1 item per page
        pagingRequest.setSize(1);
        Page<ProfileResponse<?>> resultPage1 = authService.getUserInfoFromSearch("testName", pagingRequest);

        pagingRequest.setPage(2); // Move to next page
        Page<ProfileResponse<?>> resultPage2 = authService.getUserInfoFromSearch("testName", pagingRequest);

        // Verify the response of page 1
        assertEquals(2, resultPage1.getTotalElements());
        assertEquals(2, resultPage1.getTotalPages());
        assertEquals(1, resultPage1.getContent().size());

        // Verify the response of page 2
        assertEquals(1, resultPage2.getContent().size());

        // Verify interactions
        verify(customerService, times(2)).searchCustomer("testName");
    }

    @Test
    @Transactional
    void testInitSuperAdmin_CreatesNewSuperAdmin() {
        String usernameAdmin = "admin";
        String passwordAdmin = "admin123";
        // Mock the absence of an existing admin
        when(userCredentialRepository.findByUsername(usernameAdmin)).thenReturn(Optional.empty());

        // Mock role retrieval and password encoding
        Role adminRole = new Role("role01", ERole.ROLE_ADMIN, LocalDateTime.now(),LocalDateTime.now());
        when(roleService.getOrSave(ERole.ROLE_ADMIN)).thenReturn(adminRole);
        when(passwordEncoder.encode(passwordAdmin)).thenReturn("hashedPassword");

        // Execute the method
        authService.initSuperAdmin();

        // Verify UserCredential creation and save
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
        UserCredential userCredential = UserCredential.builder()
                .username(usernameAdmin)
                .password("hashedPassword")
                .status(UserStatus.ACTIVE)
                .role(adminRole)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        verify(transactionService, times(1)).createBalance(userCredential.getId());

        // Capture the saved user and assert properties

        assertEquals(usernameAdmin, userCredential.getUsername());
        assertEquals("hashedPassword", userCredential.getPassword());
        assertEquals(UserStatus.ACTIVE, userCredential.getStatus());
        assertEquals(adminRole, userCredential.getRole());
    }

}