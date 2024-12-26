package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.application.DTO.request.CreatePermissionRequest;
import com.example.identityService.application.DTO.response.PermissionResponse;
import com.example.identityService.infrastructure.persistence.entity.PermissionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePermission(@MappingTarget PermissionEntity response, CreatePermissionRequest request);

    List<PermissionResponse> toListPermissionResponse(List<PermissionEntity> res);
}
