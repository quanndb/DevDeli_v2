package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_PASSWORD_NOT_BLANK")
    private String email;
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    private String password;
    @NotBlank(message = "FIELD_NOT_BLANK")
    private String fullname;
    @NotBlank(message = "FIELD_NOT_BLANK")
    private LocalDate dob;
    @NotBlank(message = "FIELD_NOT_BLANK")
    private int yoe;
    private Gender gender;
    private String address;
    private String ip;
}
