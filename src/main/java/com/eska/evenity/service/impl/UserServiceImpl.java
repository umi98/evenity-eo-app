package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.dto.request.AuthRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.DiagramData;
import com.eska.evenity.dto.response.UserResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.repository.UserCredentialRepository;
import com.eska.evenity.service.UserService;

import lombok.RequiredArgsConstructor;

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
        user.setModifiedDate(LocalDateTime.now());
        repository.save(user);
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUser(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage() - 1, pagingRequest.getSize());
        Page<UserCredential> result = repository.findAll(pageable);
//        List<UserCredential> result = repository.findAll();
        return result.map(this::mapToResponse);
    }

    @Override
    public void softDeleteById(String id) {
        UserCredential user = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        user.setStatus(UserStatus.DELETED);
        user.setModifiedDate(LocalDateTime.now());
        repository.saveAndFlush(user);
    }

    @Override
    public Integer getTotalUser() {
        return repository.findAll().size() - 1;
    }

    @Override
    public Integer UserRegisterThisMonth() {
        return repository.countUsersRegisteredThisMonth();
    }

    @Override
    public List<DiagramData> getEventCountByMonth() {
        List<UserCredential> userCredentials = repository.findByStatus(UserStatus.ACTIVE);

        LocalDateTime minDate = userCredentials.stream().map(UserCredential::getCreatedDate)
                .min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
        LocalDateTime maxDate = userCredentials.stream().map(UserCredential::getCreatedDate)
                .max(LocalDateTime::compareTo).orElse(LocalDateTime.now());

        Map<String, Long> userCountByMonth = userCredentials.stream()
                .collect(Collectors.groupingBy(
                        userCredential -> userCredential.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        List<DiagramData> result = new ArrayList<>();
        YearMonth currentMonth = YearMonth.from(minDate);
        YearMonth endMonth = YearMonth.from(maxDate);

        while (!currentMonth.isAfter(endMonth)) {
            String monthLabel = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Long count = userCountByMonth.getOrDefault(monthLabel, 0L);
            result.add(new DiagramData(monthLabel, Math.toIntExact(count)));
            currentMonth = currentMonth.plusMonths(1);
        }

        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Load by user's username failed"));
    }

    private UserResponse mapToResponse(UserCredential userCredential) {
        return UserResponse.builder()
                .id(userCredential.getId())
                .email(userCredential.getUsername())
                .role(userCredential.getRole().getRole().name())
                .status(userCredential.getStatus().name())
                .createdDate(userCredential.getCreatedDate())
                .modifiedDate(userCredential.getModifiedDate())
                .build();
    }
}
