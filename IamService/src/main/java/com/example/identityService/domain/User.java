package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.application.DTO.Gender;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.command.RegisterCommand;
import com.example.identityService.domain.command.UpdateUserCommand;
import com.example.identityService.domain.command.UpdateUserInfoCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@AllArgsConstructor
public class User extends AuditableDomain {
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

    private List<UserRole> roles = new ArrayList<>();

//    @TODO domain constructor
    public User(CreateUserCommand cmd){
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

        this.assignRoles(List.of(), cmd.getRoleIds());
    }

    public User(RegisterCommand cmd){
        this.id = UUID.randomUUID();
        this.email = cmd.getEmail();
        this.password = cmd.getPassword();
        this.fullname = cmd.getFullname();
        this.dob = cmd.getDob();
        this.yoe = cmd.getYoe();
        this.verified = false;
        this.enable = true;
        this.deleted = false;
        this.gender = cmd.getGender();
        this.address = cmd.getAddress();
        this.cloudImageUrl = cmd.getCloudImageUrl();
    }

    public void updateUserInfo(UpdateUserInfoCommand cmd){
        if (cmd == null) return;
        if ( cmd.getPassword() != null ) {
            this.password = cmd.getPassword();
        }
        if ( cmd.getFullname() != null ) {
            this.fullname = cmd.getFullname();
        }
        if ( cmd.getDob() != null ) {
            this.dob = cmd.getDob();
        }
        if ( cmd.getGender() != null ) {
            this.gender = cmd.getGender();
        }
        if ( cmd.getAddress() != null ) {
            this.address = cmd.getAddress();
        }
        if ( cmd.getCloudImageUrl() != null ) {
            this.cloudImageUrl = cmd.getCloudImageUrl();
        }
    }

    public void updateUser(List<UserRole> userRoles, UpdateUserCommand cmd){
        if ( cmd == null ) {
            return;
        }
        if ( cmd.getPassword() != null ) {
            this.password = cmd.getPassword();
        }
        if ( cmd.getFullname() != null ) {
            this.fullname = cmd.getFullname();
        }
        if ( cmd.getDob() != null ) {
            this.dob = cmd.getDob();
        }
        if ( cmd.getYoe() != null ) {
            this.yoe = cmd.getYoe();
        }
        if ( cmd.getVerified() != null ) {
            this.verified = cmd.getVerified();
        }
        if ( cmd.getDeleted() != null ) {
            this.deleted = cmd.getDeleted();
        }
        if ( cmd.getEnable() != null ) {
            this.enable = cmd.getEnable();
        }
        if ( cmd.getGender() != null ) {
            this.gender = cmd.getGender();
        }
        if ( cmd.getAddress() != null ) {
            this.address = cmd.getAddress();
        }
        if ( cmd.getCloudImageId() != null ) {
            this.cloudImageId = cmd.getCloudImageId();
        }
        if ( cmd.getCloudImageUrl() != null ) {
            this.cloudImageUrl = cmd.getCloudImageUrl();
        }
        if( cmd.getRoleIds() != null ) {
            this.assignRoles(userRoles, cmd.getRoleIds());
        }
    }

    public void deleteUser(){
        this.deleted = true;
    }

    public void updateUserEnable(boolean enable){
        this.enable = enable;
    }

    public void upDatePassword(String newPassword){
        this.password = newPassword;
    }

    private void assignRoles(List<UserRole> existingUserRoles, List<UUID> newRoleIds) {
        // Get the existing role IDs from the provided list of UserRoles
        List<UUID> existingRoleIds = existingUserRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        // Identify roles to add (new roles not in existing)
        List<UserRole> rolesToAdd = newRoleIds.stream()
                .filter(roleId -> !existingRoleIds.contains(roleId)) // New roles not in existing
                .map(roleId -> new UserRole(UUID.randomUUID(), false, this.id, roleId)) // Generate new UUID for new roles
                .toList();

        // Identify roles to update or delete
        List<UserRole> rolesToUpdateOrDelete = existingUserRoles.stream()
                .map(existingRole -> {
                    // If the role exists in newRoleIds, we update it (set deleted to false)
                    if (newRoleIds.contains(existingRole.getRoleId())) {
                        return new UserRole(existingRole.getId(), false, existingRole.getAccountId(), existingRole.getRoleId());
                    } else {
                        // If the role is not in newRoleIds, we mark it as deleted
                        return new UserRole(existingRole.getId(), true, existingRole.getAccountId(), existingRole.getRoleId());
                    }
                })
                .toList();

        // Combine roles to add and update/delete
        List<UserRole> result = new ArrayList<>();
        result.addAll(rolesToAdd);
        result.addAll(rolesToUpdateOrDelete);

        this.roles = result;
    }


/*    @TODO
        - save user (info, roles) --> Saved ok but not sync to keycloak, password encoder yet
        - update user (info, roles) --> Saved ok, soft delete and undelete
        - delete user (soft delete) --> delete ok
*/
}

/* @TODO
     - Presentation(controller: DTO) --> Application(service: domain) --> Domain(core domain, subdomain, support domain) --> Infrastructure (entity) --> Database
     - Presentation(controller: DTO) --> Application(service: entity) --> Infrastructure --> Database

 */