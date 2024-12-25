package com.example.identityService.application.DTO.response;

import com.example.identityService.application.DTO.PermissionScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RolePermissionResponse {
    private String permissionName;
    private PermissionScope scope;
}
