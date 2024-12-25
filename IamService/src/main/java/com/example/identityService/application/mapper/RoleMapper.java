package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.request.CreateRoleRequest;
import com.example.identityService.application.DTO.response.RoleResponse;
import com.example.identityService.domain.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget Role response, CreateRoleRequest request);

    List<RoleResponse> toListRoleResponse(List<Role> res);
}
