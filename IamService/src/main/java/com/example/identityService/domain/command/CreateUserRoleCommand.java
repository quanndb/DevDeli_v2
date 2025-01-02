package com.example.identityService.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserRoleCommand {
    private UUID id;
    private UUID accountId;
    private UUID roleId;
    private boolean deleted;
}
