package com.example.identityService.application.service;

import com.devdeli.common.UserAuthority;

import java.util.List;
import java.util.UUID;

public interface RolePermissionQueryService {
    UserAuthority getUserAuthorityByUserId(UUID userId);
    UserAuthority getUserAuthorityByUserEmail(String email);
    List<String> getAllRolePermissionsOfUser(UUID userId);
    List<String> getAllRolePermissionsOfRole(UUID roleId);
    List<String> getAllRolePermissionsInList(List<UUID> roleIds);
}
