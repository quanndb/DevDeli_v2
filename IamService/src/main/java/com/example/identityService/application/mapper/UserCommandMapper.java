package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.command.RegisterCommand;
import com.example.identityService.domain.command.UpdateUserCommand;
import com.example.identityService.domain.command.UpdateUserInfoCommand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserCommandMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromCreateDtoToCreateCmd(@MappingTarget CreateUserCommand response, CreateAccountRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromCreateDtoToCreateCmd(@MappingTarget RegisterCommand response, RegisterRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateDtoToUpdateCmd(@MappingTarget UpdateUserCommand response, UpdateAccountRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateDtoToUpdateCmd(@MappingTarget UpdateUserInfoCommand response, UpdateProfileRequest request);
}
