package com.example.identityService.application.service;

import com.example.identityService.application.DTO.PermissionScope;
import com.example.identityService.application.DTO.request.DetailsAssignPermissionRequest;
import com.example.identityService.infrastructure.persistence.entity.PermissionEntity;
import com.example.identityService.infrastructure.persistence.entity.RolePermissionEntity;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.infrastructure.persistence.repository.PermissionRepository;
import com.example.identityService.infrastructure.persistence.repository.RolePermissionRepository;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public boolean assignPermission(String roleId, List<DetailsAssignPermissionRequest> requests){
        roleRepository.findById(roleId).orElseThrow(() -> new AppExceptions(ErrorCode.ROLE_NOTFOUND));

        Map<String, List<PermissionScope>> permissions = new HashMap<>();

        List<PermissionEntity> foundPermissions = permissionRepository.findAllByCodeIgnoreCaseIn(
                requests.stream()
                        .map(DetailsAssignPermissionRequest::getPermissionCode)
                        .toList()
        );

        foundPermissions.forEach(item -> requests.forEach(request -> {
            if (request.getPermissionCode().equalsIgnoreCase(item.getCode())) {
                permissions.put(request.getPermissionCode(), request.getScopes());
            }
        }));

        List<RolePermissionEntity> existingRolePermissions = rolePermissionRepository.findAllByRoleIdAndDeletedIsFalse(roleId);

        List<RolePermissionEntity> saveRolePermissions = new ArrayList<>();

        permissions.forEach((permissionCode, scopes) -> {
            scopes.forEach(scope -> {
                boolean alreadyExists = existingRolePermissions.stream()
                        .anyMatch(rp -> rp.getPermissionCode().equalsIgnoreCase(permissionCode)
                                && rp.getScope().equals(scope));

                if (!alreadyExists) {
                    // Create new RolePermission entity
                    saveRolePermissions.add(RolePermissionEntity.builder()
                            .roleId(roleId)
                            .permissionCode(permissionCode.toUpperCase())
                            .scope(scope)
                            .build());
                }
            });
        });

        rolePermissionRepository.saveAll(saveRolePermissions);
        return true;
    }

    // un assign
    public boolean unAssignPermission(String roleId, List<DetailsAssignPermissionRequest> requests) {
        roleRepository.findById(roleId).orElseThrow(() -> new AppExceptions(ErrorCode.ROLE_NOTFOUND));

        List<RolePermissionEntity> existingRolePermissions = rolePermissionRepository.findAllByRoleIdAndDeletedIsFalse(roleId);
        List<RolePermissionEntity> deleteRolePermissions = new ArrayList<>();

        for (DetailsAssignPermissionRequest request : requests) {
            String permissionCode = request.getPermissionCode().toUpperCase();
            List<PermissionScope> scopesToRemove = request.getScopes();

            existingRolePermissions.stream()
                    .filter(rp -> rp.getPermissionCode().equalsIgnoreCase(permissionCode)
                            && scopesToRemove.contains(rp.getScope()))
                    .forEach(deleteRolePermissions::add);
        }

        deleteRolePermissions.forEach(rp -> rp.setDeleted(true));

        rolePermissionRepository.saveAll(deleteRolePermissions);
        return true;
    }
}
