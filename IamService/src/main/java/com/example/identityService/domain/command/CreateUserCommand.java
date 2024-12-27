package com.example.identityService.domain.command;

import com.example.identityService.application.DTO.Gender;
import com.example.identityService.domain.UserRoleDomain;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateUserCommand {
    private String email;
    private String password;
    private String fullname;
    private LocalDate dob;
    private Integer yoe;
    private Boolean verified;
    private Boolean enable;
    private Gender gender;
    private String address;
    private String cloudImageUrl;
    private String cloudImageId;
    private String ip;

    private List<UUID> roleIds;
}
