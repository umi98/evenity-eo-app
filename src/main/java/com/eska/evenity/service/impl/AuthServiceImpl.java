package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ERole;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.RegisterResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.Role;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.repository.UserCredentialRepository;
import com.eska.evenity.security.JwtUtils;
import com.eska.evenity.service.AuthService;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.RoleService;
import com.eska.evenity.service.VendorService;
import com.eska.evenity.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RoleService roleService;
    private final UserCredentialRepository userCredentialRepository;
    private final CustomerService customerService;
    private final VendorService vendorService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    private final String usernameAdmin = "admin@gmail.com";
    private final String passwordAdmin = "admin";

    @PostConstruct
    public void initSuperAdmin() {
        Optional<UserCredential> optionalUserCredential = userCredentialRepository.findByUsername(usernameAdmin);
        if (optionalUserCredential.isPresent()) return;

        Role adminRole = roleService.getOrSave(ERole.ROLE_ADMIN);

        String hashPassword = passwordEncoder.encode(passwordAdmin);
        UserCredential userCredential = UserCredential.builder()
                .username(usernameAdmin)
                .password(hashPassword)
                .role(adminRole)
                .build();
        userCredentialRepository.saveAndFlush(userCredential);
    }

    @Override
    public RegisterResponse customerRegister(CustomerRegisterRequest request) {
        try {
            Role roleCustomer = roleService.getOrSave(ERole.ROLE_CUSTOMER);
            String hashPassword = passwordEncoder.encode(request.getPassword());
            UserCredential userCredential = userCredentialRepository.saveAndFlush(
                    UserCredential.builder()
                            .username(request.getUsername())
                            .password(hashPassword)
                            .role(roleCustomer).build()
            );
            Customer customer = customerService.createCustomer(
                    (Customer.builder()
                        .fullName(request.getFullName())
                        .address(request.getAddress())
                        .phoneNumber(request.getPhoneNumber())
                        .build()
                    ), userCredential);
            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .name(customer.getFullName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RegisterResponse vendorRegister(VendorRegisterRequest request) {
        try {
            Role roleVendor = roleService.getOrSave(ERole.ROLE_VENDOR);
            String hashPassword = passwordEncoder.encode(request.getPassword());
            UserCredential userCredential = userCredentialRepository.saveAndFlush(
                    UserCredential.builder()
                            .username(request.getUsername())
                            .password(hashPassword)
                            .role(roleVendor).build()
            );
            Vendor vendor = vendorService.createVendor(
                    (Vendor.builder()
                            .name(request.getName())
                            .address(request.getAddress())
                            .phoneNumber(request.getPhoneNumber())
                            .owner(request.getOwnerName())
                            .build()
                    ), userCredential);
            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .name(vendor.getName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AuthResponse signIn(AuthRequest request) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            );
            Authentication authenticated = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            UserCredential userCredential = (UserCredential) authenticated.getPrincipal();
            return AuthResponse.builder()
                    .token(jwtUtils.generateToken(userCredential))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
