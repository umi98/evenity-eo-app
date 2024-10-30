package com.eska.evenity.controller;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.AuthResponse;
import com.eska.evenity.dto.response.RegisterResponse;
import com.eska.evenity.dto.response.UserResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.AuthService;
import com.eska.evenity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse token = authService.login(request);
        if (token.getToken() == null) {
            WebResponse<AuthResponse> response = WebResponse.<AuthResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Unauthorized")
                    .data(token)
                    .build();
            return ResponseEntity.ok(response);
        }
        WebResponse<AuthResponse> response = WebResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Login success")
                .data(token)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> userList() {
        List<UserResponse> userResponses = userService.getAllUser();
        WebResponse<List<UserResponse>> response = WebResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieve all data")
                .data(userResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable String id, @Valid @RequestBody AuthRequest request) {
        UserResponse userResponse = userService.changePassword(id, request);
        WebResponse<UserResponse> response = WebResponse.<UserResponse>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully change password")
                .data(userResponse)
                .build();
        return ResponseEntity.ok(response);
    }
}
