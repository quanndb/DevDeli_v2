package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;

import java.util.UUID;

public interface UserCommandService {
    boolean register(RegisterRequest request);
    boolean createUser(CreateAccountRequest request);
    boolean updateUser(UUID userId, UpdateAccountRequest request);
    boolean updateUserInfo(String email, UpdateProfileRequest request);
    boolean deleteUser(UUID userId);
}
