package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.constant.VendorStatus;
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
import org.springframework.web.server.ResponseStatusException;

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
}