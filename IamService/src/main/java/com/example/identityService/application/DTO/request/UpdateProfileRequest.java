package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.Gender;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    private String password;
    private String fullname;
    private String address;
    private Gender gender;
    private LocalDate dob;
    private String cloudImageUrl;
}
