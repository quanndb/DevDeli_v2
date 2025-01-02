package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.application.DTO.request.RolePageRequest;
import com.example.identityService.application.DTO.response.RoleResponse;

import java.util.List;

public interface CustomRoleRepository {
    List<RoleResponse> search(RolePageRequest request);
    Long count(RolePageRequest request);
}
