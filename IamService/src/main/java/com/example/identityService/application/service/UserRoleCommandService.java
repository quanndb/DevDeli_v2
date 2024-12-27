package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateAccountRoleRequest;
import com.example.identityService.domain.UserRoleDomain;

public interface UserRoleCommandService {
    boolean createUserRole(CreateAccountRoleRequest userRoleDomain);
}
