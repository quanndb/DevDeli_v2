package com.example.identityService.application.service;

import com.example.identityService.application.DTO.request.CreateAccountRequest;

public interface UserCommandService {
    boolean createUser(CreateAccountRequest request);
}
