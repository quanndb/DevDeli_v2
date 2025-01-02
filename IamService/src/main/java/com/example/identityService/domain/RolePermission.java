package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.application.DTO.PermissionScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission extends AuditableDomain {
    private UUID id;
    private UUID roleId;
    private String permissionCode;
    private PermissionScope scope;
    private boolean deleted;
}
