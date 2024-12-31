package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateAccountRequest {
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
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

    private List<UUID> roleIds;
}
