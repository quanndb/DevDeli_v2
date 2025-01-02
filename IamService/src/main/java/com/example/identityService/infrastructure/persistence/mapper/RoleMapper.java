package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.application.DTO.request.CreateRoleRequest;
import com.example.identityService.application.DTO.response.RoleResponse;
import com.example.identityService.domain.Role;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget RoleEntity response, CreateRoleRequest request);
    List<RoleResponse> toListRoleResponse(List<RoleEntity> request);
    List<Role> toListRoleDomain(List<RoleEntity> request);
    List<RoleEntity> toListRoleEntity(List<Role> request);
}
