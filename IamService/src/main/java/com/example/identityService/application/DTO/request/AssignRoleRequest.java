package com.example.identityService.application.DTO.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class AssignRoleRequest {
    @NotEmpty(message = "ROLE_NOT_EMPTY")
    List<UUID> roles;
}
