package com.eska.evenity.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.UserResponse;
import com.eska.evenity.entity.UserCredential;

public interface UserService extends UserDetailsService {
    UserCredential loadByUserId(String userId);
    UserCredential findByUsername(String username);
    UserResponse changePassword(String id, AuthRequest request);
    Page<UserResponse> getAllUser(PagingRequest pagingRequest);
    void softDeleteById(String id);
    Integer getTotalUser();
    Integer UserRegisterThisMonth();
}
