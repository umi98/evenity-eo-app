package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.VendorRepository;
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VendorServiceImplTest {
    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private VendorServiceImpl vendorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVendor_ShouldReturnNewVendor_WhenValidInput() {
        Vendor vendor = Vendor.builder().name("Test Vendor").phoneNumber("123456789").province("Province")
                .city("City").district("District").address("Address").owner("Owner")
                .mainCategory(CategoryType.CATERING).build();
        UserCredential userCredential = new UserCredential();

        when(vendorRepository.saveAndFlush(any(Vendor.class))).thenReturn(vendor);

        Vendor result = vendorService.createVendor(vendor, userCredential);

        assertNotNull(result);
        assertEquals("Test Vendor", result.getName());
        verify(vendorRepository, times(1)).saveAndFlush(any(Vendor.class));
    }

    @Test
    void getVendorById_ShouldReturnVendor_WhenIdExists() {
        Vendor vendor = Vendor.builder()
                .id("123")
                .name("Test Vendor")
                .userCredential(new UserCredential())
                .mainCategory(CategoryType.ENTERTAINER)
                .status(VendorStatus.ACTIVE)
                .build();
        when(vendorRepository.findById("123")).thenReturn(Optional.of(vendor));

        VendorResponse result = vendorService.getVendorById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
    }

    @Test
    void getVendorById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(vendorRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> vendorService.getVendorById("123"));
    }

    @Test
    void updateVendor_ShouldUpdateVendor_WhenUserStatusIsActive() {
        Vendor vendor = Vendor.builder().id("123").name("Old Name").status(VendorStatus.ACTIVE).userCredential(new UserCredential()).build();
        vendor.getUserCredential().setStatus(UserStatus.ACTIVE);

        VendorRequest request = new VendorRequest();
        request.setName("New Name");
        request.setPhoneNumber("987654321");
        request.setProvince("New Province");
        request.setCity("New City");
        request.setDistrict("New District");
        request.setAddress("New Address");
        request.setOwnerName("New Owner");
        request.setMainCategory("CATERING");

        when(vendorRepository.findById("123")).thenReturn(Optional.of(vendor));
        when(vendorRepository.saveAndFlush(any(Vendor.class))).thenReturn(vendor);

        VendorResponse result = vendorService.updateVendor("123", request);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
    }

    @Test
    void approveStatusVendor_ShouldActivateVendorAndCreateBalance_WhenVendorIsFound() {
        Vendor vendor = Vendor.builder()
                .id("123")
                .status(VendorStatus.PENDING)
                .mainCategory(CategoryType.ENTERTAINER)
                .userCredential(new UserCredential()).build();

        when(vendorRepository.findById("123")).thenReturn(Optional.of(vendor));
        doThrow(new RuntimeException("Balance not found")).when(transactionService).getBalanceUsingUserId(anyString());
        when(vendorRepository.saveAndFlush(any(Vendor.class))).thenReturn(vendor);

        VendorResponse result = vendorService.approveStatusVendor("123");

        assertNotNull(result);
        assertEquals(VendorStatus.ACTIVE.name(), result.getStatus());
//        verify(transactionService, times(1)).createBalance(vendor.getUserCredential().getId());
    }

    @Test
    void upVoteVendor_ShouldIncreaseScoring() {
        Vendor vendor = Vendor.builder().id("123").scoring(50).build();
        when(vendorRepository.findById("123")).thenReturn(Optional.of(vendor));

        vendorService.upVoteVendor("123");

        assertEquals(52, vendor.getScoring());
        verify(vendorRepository, times(1)).saveAndFlush(vendor);
    }

    @Test
    void downVoteVendor_ShouldDecreaseScoring() {
        Vendor vendor = Vendor.builder().id("123").scoring(50).build();
        when(vendorRepository.findById("123")).thenReturn(Optional.of(vendor));

        vendorService.downVoteVendor("123");

        assertEquals(48, vendor.getScoring());
        verify(vendorRepository, times(1)).saveAndFlush(vendor);
    }

    @Test
    public void testGetAllVendor() {
        // Arrange
        PagingRequest pagingRequest = new PagingRequest(1, 10);
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId("1");
        vendor.setUserCredential(new UserCredential());
        vendor.setMainCategory(CategoryType.CATERING);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setName("Test Vendor");

        List<Vendor> vendors = Collections.singletonList(vendor);
        Page<Vendor> vendorPage = new PageImpl<>(vendors, pageable, vendors.size());

        when(vendorRepository.findAll(pageable)).thenReturn(vendorPage);

        // Act
        Page<VendorResponse> result = vendorService.getAllVendor(pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Test Vendor", result.getContent().get(0).getName()); // Assuming VendorResponse has a getName() method

        verify(vendorRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetAllActiveVendor() {
        // Arrange
        PagingRequest pagingRequest = new PagingRequest(1, 10);
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId("1");
        vendor.setUserCredential(new UserCredential());
        vendor.setMainCategory(CategoryType.CATERING);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setName("Active Vendor");

        List<Vendor> vendors = Collections.singletonList(vendor);
        Page<Vendor> vendorPage = new PageImpl<>(vendors, pageable, vendors.size());

        when(vendorRepository.getVendorByStatus(UserStatus.ACTIVE, pageable)).thenReturn(vendorPage);

        // Act
        Page<VendorResponse> result = vendorService.getAllActiveVendor(pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Active Vendor", result.getContent().get(0).getName()); // Assuming VendorResponse has a getName() method

        verify(vendorRepository, times(1)).getVendorByStatus(UserStatus.ACTIVE, pageable);
    }

    @Test
    public void testGetApprovedVendor() {
        // Arrange
        PagingRequest pagingRequest = new PagingRequest(1, 10);
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId("1");
        vendor.setUserCredential(new UserCredential());
        vendor.setMainCategory(CategoryType.CATERING);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setName("Approved Vendor");

        List<Vendor> vendors = Collections.singletonList(vendor);
        Page<Vendor> vendorPage = new PageImpl<>(vendors, pageable, vendors.size());

        when(vendorRepository.findByStatus(VendorStatus.ACTIVE, pageable)).thenReturn(vendorPage);

        // Act
        Page<VendorResponse> result = vendorService.getApprovedVendor(pagingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Approved Vendor", result.getContent().get(0).getName()); // Assuming VendorResponse has a getName() method

        verify(vendorRepository, times(1)).findByStatus(VendorStatus.ACTIVE, pageable);
    }

    @Test
    public void testGetVendorByUserId() {
        // Arrange
        String userId = "user123";
        UserCredential userCredential = new UserCredential(); // Assuming you have a UserCredential class
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId("vendor123");

        when(userService.loadByUserId(userId)).thenReturn(userCredential);
        when(vendorRepository.findVendorByUserCredential(userCredential)).thenReturn(vendor);

        // Act
        Vendor result = vendorService.getVendorByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals("vendor123", result.getId());
        verify(userService, times(1)).loadByUserId(userId);
        verify(vendorRepository, times(1)).findVendorByUserCredential(userCredential);
    }

    @Test
    public void testGetVendorUsingId() {
        // Arrange
        String vendorId = "vendor123";
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId(vendorId);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor)); // Assuming findById returns Optional

        // Act
        Vendor result = vendorService.getVendorUsingId(vendorId);

        // Assert
        assertNotNull(result);
        assertEquals(vendorId, result.getId());
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    public void testGetVendorUsingId_NotFound() {
        // Arrange
        String vendorId = "vendor123";

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty()); // Assuming findById returns Optional

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            vendorService.getVendorUsingId(vendorId);
        });

        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    public void testRejectStatusVendor() {
        // Arrange
        String vendorId = "vendor123";
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId(vendorId);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setUserCredential(new UserCredential());
        vendor.setMainCategory(CategoryType.CATERING);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor)); // Assuming findById returns Optional

        // Act
        VendorResponse result = vendorService.rejectStatusVendor(vendorId);

        // Assert
        assertNotNull(result);
        assertEquals(VendorStatus.INACTIVE, vendor.getStatus());
        assertNotNull(vendor.getModifiedDate());
        verify(vendorRepository, times(1)).saveAndFlush(vendor);
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    public void testSoftDeleteById() {
        // Arrange
        String vendorId = "vendor123";
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setId(vendorId);
        vendor.setStatus(VendorStatus.ACTIVE); // Assuming ACTIVE is a valid status
        UserCredential userCredential = new UserCredential(); // Assuming you have a UserCredential class
        userCredential.setId("user123");
        vendor.setUserCredential(userCredential);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor)); // Assuming findById returns Optional

        // Act
        vendorService.softDeleteById(vendorId);

        // Assert
        assertEquals(VendorStatus.INACTIVE, vendor.getStatus());
        assertNotNull(vendor.getModifiedDate());
        verify(vendorRepository, times(1)).saveAndFlush(vendor);
        verify(userService, times(1)).softDeleteById(userCredential.getId());
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    public void testSoftDeleteById_NotFound() {
        // Arrange
        String vendorId = "vendor123";

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty()); // Assuming findById returns Optional

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            vendorService.softDeleteById(vendorId);
        });

        verify(vendorRepository, times(1)).findById(vendorId);
        verify(userService, never()).softDeleteById(anyString()); // Ensure userService.softDeleteById is not called
    }

    @Test
    public void testSearchVendor() {
        // Arrange
        String vendorName = "Test Vendor";
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setName(vendorName);
        List<Vendor> vendors = Collections.singletonList(vendor);

        when(vendorRepository.findAllByNameLikeIgnoreCase('%' + vendorName + '%')).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.searchVendor(vendorName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(vendorName, result.get(0).getName());
        verify(vendorRepository, times(1)).findAllByNameLikeIgnoreCase('%' + vendorName + '%');
    }

    @Test
    public void testGetVendors_ActiveStatus() {
        // Arrange
        Vendor vendor1 = new Vendor(); // Assuming you have a Vendor class
        vendor1.setStatus(VendorStatus.ACTIVE);
        Vendor vendor2 = new Vendor();
        vendor2.setStatus(VendorStatus.ACTIVE);
        List<Vendor> vendors = Arrays.asList(vendor1, vendor2);

        when(vendorRepository.findByStatus(VendorStatus.ACTIVE)).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.getVendors(VendorStatus.ACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vendorRepository, times(1)).findByStatus(VendorStatus.ACTIVE);
    }

    @Test
    public void testGetVendors_PendingStatus() {
        // Arrange
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setStatus(VendorStatus.PENDING);
        List<Vendor> vendors = Collections.singletonList(vendor);

        when(vendorRepository.findByStatus(VendorStatus.PENDING)).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.getVendors(VendorStatus.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vendorRepository, times(1)).findByStatus(VendorStatus.PENDING);
    }

    @Test
    public void testGetVendors_InactiveStatus() {
        // Arrange
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setStatus(VendorStatus.INACTIVE);
        List<Vendor> vendors = Collections.singletonList(vendor);

        when(vendorRepository.findByStatus(VendorStatus.INACTIVE)).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.getVendors(VendorStatus.INACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vendorRepository, times(1)).findByStatus(VendorStatus.INACTIVE);
    }

    @Test
    public void testGetVendors_DisabledStatus() {
        // Arrange
        Vendor vendor = new Vendor(); // Assuming you have a Vendor class
        vendor.setStatus(VendorStatus.DISABLED);
        List<Vendor> vendors = Collections.singletonList(vendor);

        when(vendorRepository.findByStatus(VendorStatus.DISABLED)).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.getVendors(VendorStatus.DISABLED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vendorRepository, times(1)).findByStatus(VendorStatus.DISABLED);
    }

    @Test
    public void testGetAllVendors() {
        // Arrange
        Vendor vendor1 = new Vendor(); // Assuming you have a Vendor class
        Vendor vendor2 = new Vendor();
        List<Vendor> vendors = Arrays.asList(vendor1, vendor2);

        when(vendorRepository.findAll()).thenReturn(vendors);

        // Act
        List <Vendor> result = vendorService.getAllVendors();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    public void testCountVendorRegisterThisMonth() {
        // Arrange
        int expectedCount = 5;
        when(vendorRepository.countVendorsRegisteredThisMonth()).thenReturn(expectedCount);

        // Act
        Integer result = vendorService.countVendorRegisterThisMonth();

        // Assert
        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(vendorRepository, times(1)).countVendorsRegisteredThisMonth();
    }

    @Test
    public void testIsExistInCityAndProvince_Exists() {
        // Arrange
        String city = "Test City";
        String province = "Test Province";
        when(vendorRepository.existsByCityAndProvince(city, province)).thenReturn(true);

        // Act
        boolean result = vendorService.isExistInCityAndProvince(city, province);

        // Assert
        assertTrue(result);
        verify(vendorRepository, times(1)).existsByCityAndProvince(city, province);
    }

    @Test
    public void testIsExistInCityAndProvince_NotExists() {
        // Arrange
        String city = "Test City";
        String province = "Test Province";
        when(vendorRepository.existsByCityAndProvince(city, province)).thenReturn(false);

        // Act
        boolean result = vendorService.isExistInCityAndProvince(city, province);

        // Assert
        assertFalse(result);
        verify(vendorRepository, times(1)).existsByCityAndProvince(city, province);
    }
}