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

@Setter
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String email;
    private String fullname;
    private LocalDate dob;
    private int yoe;
    private String address;
    private Gender gender;
    private String cloudImageUrl;
    private String cloudImageId;
    private LocalDateTime createdDate;
    private String createdBy;
    private boolean verified;
    private boolean enable;
    private List<String> roles;
}
