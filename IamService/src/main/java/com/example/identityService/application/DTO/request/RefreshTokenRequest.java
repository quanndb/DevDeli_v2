package com.example.identityService.application.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    private String refreshToken;
}