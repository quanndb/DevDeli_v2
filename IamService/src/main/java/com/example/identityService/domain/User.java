package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import com.example.identityService.application.DTO.Gender;
import com.example.identityService.domain.command.CreateUserCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class User extends AuditableDomain {
    private String id;
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
    private List<Role> roles;

//    @TODO domain constructor
    public User(){
    }
/*    @TODO
        - save user (info, roles)
        - update user (info, roles)
        - delete user (soft delete)
        - get users and their roles
*/

    public boolean assignRoles(CreateUserCommand cmd, List<Role> roles){
        return true;
    }
}

/* @TODO
     - Presentation(controller: DTO) --> Application(service: domain) --> Domain(core domain, subdomain, support domain) --> Infrastructure (entity) --> Database
     - Presentation(controller: DTO) --> Application(service: entity) --> Infrastructure --> Database

 */