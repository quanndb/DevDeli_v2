package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.application.DTO.Gender;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.command.CreateUserRoleCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@AllArgsConstructor
public class UserDomain extends AuditableDomain {
    private UUID id;
    private String email;
    private String password;
    private String fullname;
    private LocalDate dob;
    private int yoe;
    private boolean verified;
    private boolean enable;
    private boolean deleted;
    private Gender gender;
    private String address;
    private String cloudImageId;
    private String cloudImageUrl;

    private List<UserRoleDomain> roles;

//    @TODO domain constructor
    public UserDomain(CreateUserCommand cmd){
        this.id = UUID.randomUUID();
        this.email = cmd.getEmail();
        this.password = cmd.getPassword();
        this.fullname = cmd.getFullname();
        this.dob = cmd.getDob();
        this.yoe = cmd.getYoe();
        this.verified = cmd.getVerified();
        this.enable = cmd.getEnable();
        this.deleted = false;
        this.gender = cmd.getGender();
        this.address = cmd.getAddress();
        this.cloudImageUrl = cmd.getCloudImageUrl();
        this.cloudImageId = cmd.getCloudImageId();

        this.roles = this.assignRoles(cmd.getRoleIds());
    }

    public List<UserRoleDomain> assignRoles(List<UUID> roleIds){
        List<UserRoleDomain> userRoleDomains = new ArrayList<>();
        for(UUID roleId : roleIds){
            userRoleDomains.add(new UserRoleDomain(new CreateUserRoleCommand(UUID.randomUUID(), this.id, roleId, false)));
        }
        return userRoleDomains;
    }

/*    @TODO
        - save user (info, roles) --> Saved ok but not sync to keycloak, password encoder yet
        - update user (info, roles)
        - delete user (soft delete)
*/
}

/* @TODO
     - Presentation(controller: DTO) --> Application(service: domain) --> Domain(core domain, subdomain, support domain) --> Infrastructure (entity) --> Database
     - Presentation(controller: DTO) --> Application(service: entity) --> Infrastructure --> Database

 */