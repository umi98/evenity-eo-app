package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ERole;
import com.eska.evenity.entity.Role;
import com.eska.evenity.repository.RoleRepository;
import com.eska.evenity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;

    @Override
    public Role getOrSave(ERole role) {
        try {
            Optional<Role> existingRole = repository.findByRole(role);
            if (existingRole.isPresent()) return existingRole.get();
            Role newRole = Role.builder()
                    .role(role)
                    .build();
            return repository.saveAndFlush(newRole);
        } catch (Exception e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong");
        }
    }
}
