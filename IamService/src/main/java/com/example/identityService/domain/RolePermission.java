package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.application.DTO.PermissionScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission extends AuditableDomain {
    private String id;
    private String roleId;
    private String permissionCode;
    private PermissionScope scope;
    private boolean deleted;
}
