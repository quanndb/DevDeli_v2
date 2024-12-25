package com.example.identityService.domain.repository;

import com.example.identityService.application.DTO.request.PermissionPageRequest;
import com.example.identityService.application.DTO.response.PermissionResponse;

import java.util.List;

public interface CustomPermissionRepository {
    List<PermissionResponse> search(PermissionPageRequest request);
    Long count(PermissionPageRequest request);
}
