package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreatePermissionRequest;
import com.example.identityService.application.DTO.request.PermissionPageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.response.PermissionResponse;
import com.example.identityService.application.util.JsonMapper;
import com.example.identityService.domain.entity.Permission;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.PermissionMapper;
import com.example.identityService.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final JsonMapper jsonMapper;

    public boolean createPermission(CreatePermissionRequest request){
        permissionRepository.findByCodeIgnoreCase(request.getCode())
                        .ifPresent(_ -> {
                            throw new AppExceptions(ErrorCode.PERMISSION_EXISTED);
                        });
        permissionRepository.save(Permission.builder()
                .name(request.getName())
                .code(request.getCode())
                .build());
        return true;
    }

    public boolean updatePermission(String roleId, CreatePermissionRequest request){
        Permission foundPermission = permissionRepository.findById(roleId)
                .orElseThrow(()-> new AppExceptions(ErrorCode.PERMISSION_NOTFOUND));
        permissionMapper.updatePermission(foundPermission, request);
        permissionRepository.save(foundPermission);
        return true;
    }

    public boolean deletePermission(String roleId){
        Permission foundPermission = permissionRepository.findById(roleId)
                .orElseThrow(()-> new AppExceptions(ErrorCode.ROLE_NOTFOUND));
        foundPermission.setDeleted(true);
        permissionRepository.save(foundPermission);
        return true;
    }

    public PageResponse<PermissionResponse> getPermissions(PermissionPageRequest request) {
        long totalRecords = permissionRepository.count(request);
        List<PermissionResponse> permissionResponses = permissionRepository.search(request);
        return PageResponse.<PermissionResponse>builder()
                .page(request.getPage())
                .size(request.getSize())
                .query(request.getQuery())
                .sortedBy(request.getSortedBy())
                .sortDirection(request.getSortDirection().name())
                .first(request.getPage() == 1)
                .last(request.getPage() % request.getSize() == request.getPage())
                .totalRecords(totalRecords)
                .totalPages(request.getPage() % request.getSize())
                .response(permissionResponses)
                .build();
    }
}
