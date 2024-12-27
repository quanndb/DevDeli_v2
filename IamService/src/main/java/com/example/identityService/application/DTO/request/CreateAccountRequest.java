package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CreateAccountRequest {
    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_PASSWORD_NOT_BLANK")
    private String email;
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    private String password;
    @NotBlank(message = "FIELD_NOT_BLANK")
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

    @NotEmpty(message = "ROLE_NOT_EMPTY")
    private List<UUID> roleIds;
}
