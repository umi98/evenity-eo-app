package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.dto.JwtClaim;
import com.eska.evenity.dto.response.*;
import com.eska.evenity.entity.*;
import com.eska.evenity.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eska.evenity.constant.ERole;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.repository.UserCredentialRepository;
import com.eska.evenity.security.JwtUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RoleService roleService;
    private final UserCredentialRepository userCredentialRepository;
    private final CustomerService customerService;
    private final VendorService vendorService;
    private final TransactionService transactionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin.email}")
    private String usernameAdmin;
    @Value("${app.admin.password}")
    private String passwordAdmin;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin() {
        Optional<UserCredential> optionalUserCredential = userCredentialRepository.findByUsername(usernameAdmin);
        if (optionalUserCredential.isPresent()) return;
        Role adminRole = roleService.getOrSave(ERole.ROLE_ADMIN);
        String hashPassword = passwordEncoder.encode(passwordAdmin);
        UserCredential userCredential = UserCredential.builder()
                .username(usernameAdmin)
                .password(hashPassword)
                .status(UserStatus.ACTIVE)
                .role(adminRole)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        userCredentialRepository.saveAndFlush(userCredential);
        transactionService.createBalance(userCredential.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse customerRegister(CustomerRegisterRequest request) {
        try {
            Role roleCustomer = roleService.getOrSave(ERole.ROLE_CUSTOMER);
            String hashPassword = passwordEncoder.encode(request.getPassword());
            UserCredential userCredential = userCredentialRepository.saveAndFlush(
                    UserCredential.builder()
                            .username(request.getEmail())
                            .password(hashPassword)
                            .status(UserStatus.ACTIVE)
                            .role(roleCustomer)
                            .createdDate(LocalDateTime.now())
                            .modifiedDate(LocalDateTime.now())
                            .build()
            );
            Customer customer = customerService.createCustomer(
                    (Customer.builder()
                            .fullName(request.getFullName())
                            .province(request.getProvince())
                            .city(request.getCity())
                            .district(request.getDistrict())
                            .address(request.getAddress())
                            .phoneNumber(request.getPhoneNumber())
                            .build()
                    ), userCredential);
            return RegisterResponse.builder()
                    .email(userCredential.getUsername())
                    .name(customer.getFullName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse vendorRegister(VendorRegisterRequest request) {
        try {
            Role roleVendor = roleService.getOrSave(ERole.ROLE_VENDOR);
            String hashPassword = passwordEncoder.encode(request.getPassword());
            UserCredential userCredential = userCredentialRepository.saveAndFlush(
                    UserCredential.builder()
                            .username(request.getEmail())
                            .password(hashPassword)
                            .role(roleVendor)
                            .status(UserStatus.ACTIVE)
                            .createdDate(LocalDateTime.now())
                            .modifiedDate(LocalDateTime.now())
                            .build()
            );
            Vendor vendor = vendorService.createVendor(
                    (Vendor.builder()
                            .name(request.getName())
                            .province(request.getProvince())
                            .city(request.getCity())
                            .district(request.getDistrict())
                            .address(request.getAddress())
                            .phoneNumber(request.getPhoneNumber())
                            .owner(request.getOwnerName())
                            .mainCategory(CategoryType.valueOf(request.getMainCategory()))
                            .build()
                    ), userCredential);
            return RegisterResponse.builder()
                    .email(userCredential.getUsername())
                    .name(vendor.getName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );
            Authentication authenticated = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            UserCredential userCredential = (UserCredential) authenticated.getPrincipal();
            if (userCredential.getStatus() == UserStatus.ACTIVE){
                return AuthResponse.builder()
                        .token(jwtUtils.generateToken(userCredential))
                        .build();
            } else {
                return AuthResponse.builder()
                        .message("Account is not active")
                        .build();
            }
        } catch (Exception e) {
            return AuthResponse.builder()
                    .message("Invalid username and password")
                    .build();
        }
    }

    @Override
    public ProfileResponse<?> getUserInfoUsingToken(String token) {
        JwtClaim claim = jwtUtils.getUserInfoByToken(token);
        UserCredential user = userCredentialRepository.findById(claim.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        Customer customer = customerService.getCustomerByUserId(user.getId());
        Vendor vendor = vendorService.getVendorByUserId(user.getId());
        if (customer != null) {
            return mapToResponseCustomer(customer, user);
        } else if (vendor != null){
            return mapToResponseVendor(vendor, user);
        }
        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getUsername())
                .role(user.getRole().getRole().name())
                .createdAt(user.getCreatedDate())
                .modifiedAt(user.getModifiedDate())
                .build();
    }

    @Override
    public List<ProfileResponse<?>> getUserInfoFromSearch(String name) {
        List<ProfileResponse<?>> responses = new ArrayList<>();
        List<Customer> customerList = customerService.searchCustomer(name);
        System.out.println(name);
        for (Customer customer : customerList) {
            Optional<UserCredential> user = userCredentialRepository.findById(customer.getUserCredential().getId());
            ProfileResponse<?> cust = mapToResponseCustomer(customer, user.get());
            responses.add(cust);
        }

        List<Vendor> vendorList = vendorService.searchVendor(name);
        for (Vendor vendor : vendorList) {
            Optional<UserCredential> user = userCredentialRepository.findById(vendor.getUserCredential().getId());
            ProfileResponse<?> ven = mapToResponseVendor(vendor, user.get());
            responses.add(ven);
        }
        return responses;
    }

    private ProfileResponse<?> mapToResponseCustomer(Customer customer, UserCredential user) {
        CustomerResponse customerResponse = CustomerResponse.builder()
                .userId(user.getId())
                .email(user.getUsername())
                .customerId(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .province(customer.getProvince())
                .city(customer.getCity())
                .district(customer.getDistrict())
                .address(customer.getAddress())
                .createdDate(customer.getCreatedDate())
                .modifiedDate(customer.getModifiedDate())
                .build();
        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getUsername())
                .role(user.getRole().getRole().name())
                .createdAt(user.getCreatedDate())
                .modifiedAt(user.getModifiedDate())
                .data(customerResponse)
                .build();
    }

    private ProfileResponse<?> mapToResponseVendor(Vendor vendor, UserCredential user) {
        VendorResponse vendorResponse = VendorResponse.builder()
                .userId(user.getId())
                .id(vendor.getId())
                .email(user.getUsername())
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .province(vendor.getProvince())
                .city(vendor.getCity())
                .district(vendor.getDistrict())
                .address(vendor.getAddress())
                .owner(vendor.getOwner())
                .scoring(vendor.getScoring())
                .status(vendor.getStatus().name())
                .createdDate(vendor.getCreatedDate())
                .modifiedDate(vendor.getModifiedDate())
                .build();
        return ProfileResponse.builder()
                .email(user.getUsername())
                .userId(user.getId())
                .role(user.getRole().getRole().name())
                .createdAt(user.getCreatedDate())
                .modifiedAt(user.getModifiedDate())
                .data(vendorResponse)
                .build();
    }
}
