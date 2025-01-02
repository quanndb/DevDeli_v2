package com.example.identityService.domain.command;

import com.example.identityService.application.DTO.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateUserCommand {
    private String password;
    private String fullname = "";
    private LocalDate dob;
    private Integer yoe = 0;
    private Boolean verified = false;
    private Boolean enable = true;
    private Boolean deleted = false;
    private Gender gender = Gender.MALE;
    private String address = "";
    private String cloudImageUrl;
    private String cloudImageId;

    private List<UUID> roleIds;
}
