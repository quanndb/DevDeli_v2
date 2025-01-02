package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.domain.User;

public interface CustomUserQueryMapper extends UserQueryMapper {
    UserResponse from(User user);
}
