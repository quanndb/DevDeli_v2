package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.domain.command.CreateUserCommand;

public interface CustomUserCommandMapper {
    CreateUserCommand from(CreateAccountRequest request);
}
