package com.example.identityService.infrastructure.persistence.entity;

import com.devdeli.common.Auditable;
import com.example.identityService.application.DTO.PermissionScope;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "role_permission")
@RequiredArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity extends Auditable {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID roleId;
    @Column(nullable = false)
    private String permissionCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionScope scope;
    @Column(nullable = false)
    private boolean deleted;
}
