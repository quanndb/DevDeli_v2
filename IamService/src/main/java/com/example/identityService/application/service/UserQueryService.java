package com.example.identityService.application.service;

import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.DTO.response.UserResponse;

import java.util.UUID;

public interface UserQueryService {
    LoginResponse login(LoginRequest query);
    PageResponse<UserResponse> getUsers(UserPageRequest pageRequest);
    UserResponse getUserInfo(UUID userId);
}
