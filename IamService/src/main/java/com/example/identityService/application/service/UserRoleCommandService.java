package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateAccountRoleRequest;

public interface UserRoleCommandService {
    boolean createUserRole(CreateAccountRoleRequest userRoleDomain);
}
