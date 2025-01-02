package com.example.identityService.domain.command;

import com.example.identityService.application.DTO.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterCommand {
    private String email;
    private String password;
    private String fullname = "";
    private LocalDate dob;
    private Integer yoe = 0;
    private Gender gender = Gender.MALE;
    private String address = "";
    private String cloudImageUrl;
}
