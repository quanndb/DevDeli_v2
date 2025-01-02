package com.example.identityService.application.DTO.response;

import com.example.identityService.application.DTO.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String email;
    private String fullname;
    private LocalDate dob;
    private Integer yoe;
    private String address;
    private Gender gender;
    private String cloudImageUrl;
    private String cloudImageId;
    private LocalDateTime createdDate;
    private String createdBy;
    private Boolean verified;
    private Boolean enable;
    private Boolean deleted;
    private List<String> roles;
}
