package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.domain.User;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    UserResponse toUserResponse(AccountEntity request);
    AccountEntity toAccount(User request);
    List<AccountEntity> toAccount(List<User> request);
    List<UserResponse> toListUserResponse(List<AccountEntity> request);
    RegisterRequest toRegisterRequest(User request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccount(@MappingTarget AccountEntity response, UpdateProfileRequest request);
}
