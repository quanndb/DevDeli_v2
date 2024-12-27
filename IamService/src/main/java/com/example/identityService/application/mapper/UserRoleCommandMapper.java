package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.request.CreateAccountRoleRequest;
import com.example.identityService.domain.command.CreateUserRoleCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRoleCommandMapper {
    CreateUserRoleCommand from(CreateAccountRoleRequest request);
}
