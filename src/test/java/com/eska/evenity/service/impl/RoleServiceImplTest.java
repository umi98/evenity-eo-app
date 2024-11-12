package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ERole;
import com.eska.evenity.entity.Role;
import com.eska.evenity.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrSave_ExistingRole() {
        ERole roleType = ERole.ROLE_CUSTOMER;
        Role existingRole = Role.builder()
                .role(roleType)
                .build();
        when(roleRepository.findByRole(roleType)).thenReturn(Optional.of(existingRole));
        Role result = roleService.getOrSave(roleType);
        assertEquals(existingRole, result);
        verify(roleRepository, times(0)).saveAndFlush(any(Role.class));
    }

    @Test
    void testGetOrSave_NewRole() {
        ERole roleType = ERole.ROLE_ADMIN;
        when(roleRepository.findByRole(roleType)).thenReturn(Optional.empty());
        Role newRole = Role.builder().role(roleType).build();
        when(roleRepository.saveAndFlush(any(Role.class))).thenReturn(newRole);

        Role result = roleService.getOrSave(roleType);
        assertEquals(roleType, result.getRole());
        verify(roleRepository, times(1)).saveAndFlush(any(Role.class));
    }

    @Test
    void testGetOrSave_ThrowsResponseStatusException() {
        ERole roleType = ERole.ROLE_VENDOR;
        when(roleRepository.findByRole(roleType)).thenThrow(new RuntimeException("Database error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            roleService.getOrSave(roleType);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Something went wrong", exception.getReason());
    }
}