package com.eska.evenity.service.impl;

import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.VendorRepository;
import com.eska.evenity.service.UserService;
import com.eska.evenity.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;
    private final UserService userService;

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
                .createdDate(Date.from(Instant.now()))
                .modifiedDate(Date.from(Instant.now()))
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
    public VendorResponse getVendorById(String id) {
        Vendor result = findByIdOrThrowNotFound(id);
        return mapToResponse(result);
    }

    @Override
    public VendorResponse getVendorByUserId(String id) {
        UserCredential user = userService.loadByUserId(id);
        Vendor result = vendorRepository.findVendorByUserCredential(user);
        return mapToResponse(result);
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
