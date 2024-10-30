package com.eska.evenity.service.impl;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.response.UserResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.repository.UserCredentialRepository;
import com.eska.evenity.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserCredential loadByUserId(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Load by user id failed"));
    }

    @Override
    public UserCredential findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Load by user's username failed"));
    }

    @Override
    public UserResponse changePassword(String id, AuthRequest request) {
        UserCredential user = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Load by user id failed"));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUser() {
        List<UserCredential> result = repository.findAll();
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public void softDeleteById(String id) {
        UserCredential user = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        user.setStatus(UserStatus.DELETED);
        repository.saveAndFlush(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Load by user's username failed"));
    }

    private UserResponse mapToResponse(UserCredential userCredential) {
        return UserResponse.builder()
                .id(userCredential.getId())
                .username(userCredential.getUsername())
                .createdDate(userCredential.getCreatedDate())
                .role(userCredential.getRole().getRole().name())
                .status(userCredential.getStatus().name())
                .build();
    }
}
