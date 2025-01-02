package com.example.identityService.domain.command;

import com.example.identityService.application.DTO.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserInfoCommand {
    private String password;
    private String fullname;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private String cloudImageUrl;
}
