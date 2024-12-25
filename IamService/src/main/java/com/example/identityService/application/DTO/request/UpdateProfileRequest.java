package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.Gender;
import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String fullname;
    private String address;
    private Gender gender;
    private String cloudImageUrl;
}
