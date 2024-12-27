package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.domain.command.CreateUserRoleCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDomain extends AuditableDomain {
    private UUID id;
    private boolean deleted;
    private UUID userId;
    private UUID roleId;

    public UserRoleDomain(CreateUserRoleCommand cmd){
        this.id = UUID.randomUUID();
        this.deleted = false;
        this.roleId = cmd.getRoleId();
        this.userId = cmd.getAccountId();
    }
}
