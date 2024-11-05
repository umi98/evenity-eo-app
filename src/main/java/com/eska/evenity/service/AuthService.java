package com.eska.evenity.service;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.ProfileResponse;
import com.eska.evenity.dto.response.RegisterResponse;

import java.util.List;

public interface AuthService {
    RegisterResponse customerRegister(CustomerRegisterRequest request);
    RegisterResponse vendorRegister(VendorRegisterRequest request);
    AuthResponse login(AuthRequest request);
    ProfileResponse<?> getUserInfoUsingToken(String token);
    List<ProfileResponse<?>> getUserInfoFromSearch(String name);
}
