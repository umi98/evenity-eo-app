package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.dto.request.PagingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.UserService;
import com.eska.evenity.service.VendorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;
    private final UserService userService;
//    private final ProductService productService;
    private final TransactionService transactionService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Vendor createVendor(Vendor vendor, UserCredential userCredential) {
        Vendor newVendor = Vendor.builder()
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .province(vendor.getProvince())
                .city(vendor.getCity())
                .district(vendor.getDistrict())
                .address(vendor.getAddress())
                .owner(vendor.getOwner())
                .mainCategory(vendor.getMainCategory())
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
    public Page<VendorResponse> getAllVendor(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Vendor> result = vendorRepository.findAll(pageable);
        return result.map(this::mapToResponse);
    }

    @Override
    public Page<VendorResponse> getAllActiveVendor(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Vendor> result = vendorRepository.getVendorByStatus(UserStatus.ACTIVE, pageable);
        return result.map(this::mapToResponse);
    }

    @Override
    public Page<VendorResponse> getApprovedVendor(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<Vendor> result = vendorRepository.findByStatus(VendorStatus.ACTIVE, pageable);
        return result.map(this::mapToResponse);
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

    @Override
    public Vendor getVendorUsingId(String id) {
        return findByIdOrThrowNotFound(id);
    }

//    @Override
//    public VendorWithProductsResponse getVendorWithProducts(String id) {
//        Vendor vendor = findByIdOrThrowNotFound(id);
//        List<ProductResponse> productResponses = productService.getProductsByVendorId(id);
//        return VendorWithProductsResponse.builder()
//                .id(vendor.getId())
//                .email(vendor.getUserCredential().getUsername())
//                .name(vendor.getName())
//                .phoneNumber(vendor.getPhoneNumber())
//                .province(vendor.getProvince())
//                .city(vendor.getCity())
//                .district(vendor.getDistrict())
//                .address(vendor.getAddress())
//                .owner(vendor.getOwner())
//                .scoring(vendor.getScoring())
//                .status(vendor.getStatus().name())
//                .createdDate(vendor.getCreatedDate())
//                .modifiedDate(vendor.getModifiedDate())
//                .productList(productResponses)
//                .build();
//    }

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
            vendor.setProvince(request.getProvince());
            vendor.setCity(request.getCity());
            vendor.setDistrict(request.getDistrict());
            vendor.setAddress(request.getAddress());
            vendor.setOwner(request.getOwnerName());
            vendor.setMainCategory(CategoryType.valueOf(request.getMainCategory()));
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
        try {
            transactionService.getBalanceUsingUserId(vendor.getUserCredential().getId());
            return mapToResponse(vendor);
        } catch (Exception e) {
            transactionService.createBalance(vendor.getUserCredential().getId());
            return mapToResponse(vendor);
        }
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

    @Override
    public void upVoteVendor(String id) {
        Vendor result = findByIdOrThrowNotFound(id);
        result.setScoring(result.getScoring() + 2);
        result.setModifiedDate(LocalDateTime.now());
        vendorRepository.saveAndFlush(result);
    }

    @Override
    public void downVoteVendor(String id) {
        Vendor result = findByIdOrThrowNotFound(id);
        result.setScoring(result.getScoring() - 2);
        result.setModifiedDate(LocalDateTime.now());
        vendorRepository.saveAndFlush(result);
    }

    @Override
    public List<Vendor> searchVendor(String name) {
        return vendorRepository.findAllByNameLikeIgnoreCase('%' + name + '%');
    }

    private Vendor findByIdOrThrowNotFound(String id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return vendor.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vendor not found"));
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        return VendorResponse.builder()
                .userId(vendor.getUserCredential().getId())
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
                .build();
    }
}
