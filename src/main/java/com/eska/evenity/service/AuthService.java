package com.eska.evenity.service;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse customerRegister(CustomerRegisterRequest request);
    RegisterResponse vendorRegister(VendorRegisterRequest request);
    AuthResponse signIn(AuthRequest request);
}
