package com.example.identityService.application.DTO.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAccountRoleRequest {
    private UUID id;
    private UUID roleId;
    private UUID accountId;
    private boolean deleted;
}
