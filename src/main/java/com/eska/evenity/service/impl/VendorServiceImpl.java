package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.VendorRepository;
import com.eska.evenity.service.UserService;
import com.eska.evenity.service.VendorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Vendor createVendor(Vendor vendor, UserCredential userCredential) {
        Vendor newVendor = Vendor.builder()
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .address(vendor.getAddress())
                .owner(vendor.getOwner())
                .scoring(50)
                .status(VendorStatus.PENDING)
                .userCredential(userCredential)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        vendorRepository.saveAndFlush(newVendor);
        return newVendor;
    }

    @Override
    public List<VendorResponse> getAllVendor() {
        List<Vendor> result = vendorRepository.findAll();
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<VendorResponse> getAllActiveVendor() {
        List<Vendor> result = vendorRepository.getVendorByStatus(UserStatus.ACTIVE);
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public VendorResponse getVendorById(String id) {
        Vendor result = findByIdOrThrowNotFound(id);
        return mapToResponse(result);
    }

    @Override
    public Vendor getVendorByUserId(String id) {
        UserCredential user = userService.loadByUserId(id);
        return vendorRepository.findVendorByUserCredential(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public VendorResponse updateVendor(String id, VendorRequest request) {
        try {
            Vendor vendor = findByIdOrThrowNotFound(id);
            if (vendor.getUserCredential().getStatus() != UserStatus.ACTIVE){
                throw new RuntimeException("User status is not active");
            }
            vendor.setName(request.getName());
            vendor.setPhoneNumber(request.getPhoneNumber());
            vendor.setAddress(request.getAddress());
            vendor.setOwner(request.getOwnerName());
            vendor.setModifiedDate(LocalDateTime.now());
            vendorRepository.saveAndFlush(vendor);
            return mapToResponse(vendor);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VendorResponse approveStatusVendor(String id) {
        Vendor vendor = findByIdOrThrowNotFound(id);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setModifiedDate(LocalDateTime.now());
        vendorRepository.saveAndFlush(vendor);
        return mapToResponse(vendor);
    }

    @Override
    public VendorResponse rejectStatusVendor(String id) {
        Vendor vendor = findByIdOrThrowNotFound(id);
        vendor.setStatus(VendorStatus.INACTIVE);
        vendor.setModifiedDate(LocalDateTime.now());
        vendorRepository.saveAndFlush(vendor);
        return mapToResponse(vendor);
    }

    @Override
    public void softDeleteById(String id) {
        try {
            Vendor vendor = findByIdOrThrowNotFound(id);
            vendor.setStatus(VendorStatus.INACTIVE);
            vendor.setModifiedDate(LocalDateTime.now());
            vendorRepository.saveAndFlush(vendor);
            String userCredential = vendor.getUserCredential().getId();
            userService.softDeleteById(userCredential);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Vendor findByIdOrThrowNotFound(String id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return vendor.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vendor not found"));
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .address(vendor.getAddress())
                .owner(vendor.getOwner())
                .scoring(vendor.getScoring())
                .status(vendor.getStatus().name())
                .build();
    }
}
