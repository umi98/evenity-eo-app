package com.eska.evenity.controller;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.dto.response.RegisterResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<WebResponse<RegisterResponse>> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        RegisterResponse registerResponse = authService.customerRegister(request);
        WebResponse<RegisterResponse> response = WebResponse.<RegisterResponse>builder()
                .status(HttpStatus.CREATED.getReasonPhrase())
                .message("Register success")
                .data(registerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/vendor")
    public ResponseEntity<WebResponse<RegisterResponse>> registerVendor(@Valid @RequestBody VendorRegisterRequest request) {
        RegisterResponse registerResponse = authService.vendorRegister(request);
        WebResponse<RegisterResponse> response = WebResponse.<RegisterResponse>builder()
                .status(HttpStatus.CREATED.getReasonPhrase())
                .message("Register success")
                .data(registerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest request) {
        AuthResponse token = authService.signIn(request);
        WebResponse<AuthResponse> response = WebResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Login success")
                .data(token)
                .build();
        return ResponseEntity.ok(response);
    }
}
