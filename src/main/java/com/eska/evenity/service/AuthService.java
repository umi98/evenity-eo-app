package com.eska.evenity.service;

import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.ProfileResponse;
import com.eska.evenity.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse customerRegister(CustomerRegisterRequest request);
    RegisterResponse vendorRegister(VendorRegisterRequest request);
    AuthResponse login(AuthRequest request);
    ProfileResponse<?> getUserInfoUsingToken(String token);
    Page<ProfileResponse<?>> getUserInfoFromSearch(String name, PagingRequest pagingRequest);
}
