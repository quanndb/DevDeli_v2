package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.domain.RolePermission;
import com.example.identityService.infrastructure.persistence.entity.RolePermissionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {
    List<RolePermission> toListRolePermissionDomain(List<RolePermissionEntity> request);
    List<RolePermissionEntity> toListRolePermissionEntity(List<RolePermission> request);
}
