package com.eska.evenity.controller;

import com.auth0.jwt.interfaces.Claim;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.CustomerRegisterRequest;
import com.eska.evenity.dto.request.VendorRegisterRequest;
import com.eska.evenity.dto.response.*;
import com.eska.evenity.service.AuthService;
import com.eska.evenity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        try {
            RegisterResponse registerResponse = authService.customerRegister(request);
            WebResponse<RegisterResponse> response = WebResponse.<RegisterResponse>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Register success")
                    .data(registerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/register/vendor")
    public ResponseEntity<?> registerVendor(@Valid @RequestBody VendorRegisterRequest request) {
        try {
            RegisterResponse registerResponse = authService.vendorRegister(request);
            WebResponse<RegisterResponse> response = WebResponse.<RegisterResponse>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .message("Register success")
                    .data(registerResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse token = authService.login(request);
        if (token.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            WebResponse<AuthResponse> response = WebResponse.<AuthResponse>builder()
                    .status(HttpStatus.OK.name())
                    .message("Login success")
                    .data(token)
                    .build();
            return ResponseEntity.ok(response);
        }
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

    @GetMapping("/user/info")
    public ResponseEntity<?> checkUserInfoUsingToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                String token = authHeader.substring(7);
                ProfileResponse<?> profileResponse = authService.getUserInfoUsingToken(token);
                WebResponse<?> response = WebResponse.builder()
                        .status(HttpStatus.OK.getReasonPhrase())
                        .message("Successfully retrieve data")
                        .data(profileResponse)
                        .build();
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/user/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable String id, @Valid @RequestBody AuthRequest request) {
        try {
            UserResponse userResponse = userService.changePassword(id, request);
            WebResponse<UserResponse> response = WebResponse.<UserResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully change password")
                    .data(userResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
