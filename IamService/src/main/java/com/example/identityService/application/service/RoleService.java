package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateRoleRequest;
import com.example.identityService.application.DTO.request.RolePageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.response.RoleResponse;
import com.example.identityService.domain.entity.Role;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.RoleMapper;
import com.example.identityService.domain.repository.RolePermissionRepository;
import com.example.identityService.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RolePermissionRepository rolePermissionRepository;

    public List<String> getAllRolePermission(String roleId){
        return rolePermissionRepository.findAllByRoleIdAndDeletedIsFalse(roleId)
                .stream()
                .map(item -> item.getPermissionCode().toLowerCase() + "." + item.getScope().toString().toLowerCase())
                .toList();
    }

    public boolean createRole(CreateRoleRequest request){
        roleRepository.findByNameIgnoreCase(request.getName())
                        .ifPresent( _ -> {
                            throw new AppExceptions(ErrorCode.ROLE_EXISTED);
                        });
        roleRepository.save(Role.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                .build());
        return true;
    }

    public boolean updateRole(String roleId, CreateRoleRequest request){
        Role foundRole = roleRepository.findById(roleId)
                .orElseThrow(()-> new AppExceptions(ErrorCode.ROLE_NOTFOUND));
        roleMapper.updateRole(foundRole, request);
        roleRepository.save(foundRole);
        return true;
    }

    public boolean deleteRole(String roleId){
        Role foundRole = roleRepository.findById(roleId)
                .orElseThrow(()-> new AppExceptions(ErrorCode.ROLE_NOTFOUND));
        foundRole.setDeleted(true);
        roleRepository.save(foundRole);
        return true;
    }

    public PageResponse<RoleResponse> getRoles(RolePageRequest request) {
        long totalRecords = roleRepository.count(request);
        List<RoleResponse> roleResponses = roleRepository.search(request);
        return PageResponse.<RoleResponse>builder()
                .page(request.getPage())
                .size(request.getSize())
                .query(request.getQuery())
                .sortedBy(request.getSortedBy())
                .sortDirection(request.getSortDirection().name())
                .first(request.getPage() == 1)
                .last(request.getPage() % request.getSize() == request.getPage())
                .totalRecords(totalRecords)
                .totalPages(request.getPage() % request.getSize())
                .response(roleResponses)
                .build();
    }
}
