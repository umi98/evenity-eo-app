package com.eska.evenity.service;

import com.eska.evenity.constant.ERole;
import com.eska.evenity.entity.Role;

public interface RoleService {
    Role getOrSave(ERole role);
}
