package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserCommandService {
    boolean register(RegisterRequest request);
    boolean createUser(CreateAccountRequest request);
    boolean updateUser(UUID userId, UpdateAccountRequest request);
    boolean updateUserInfo(String email, UpdateProfileRequest request);
    boolean changePassword(String email, String oldPassword, String newPassword);
    boolean resetPassword(String token, String newPassword);
    boolean setUserEnable(UUID userId, boolean enable);
    boolean deleteUser(UUID userId);
    List<String> importUsers(MultipartFile multipartFile, String ownerId) throws IOException;
}
