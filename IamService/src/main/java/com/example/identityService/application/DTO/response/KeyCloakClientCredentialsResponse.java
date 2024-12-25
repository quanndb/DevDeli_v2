package com.example.identityService.application.DTO.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeyCloakClientCredentialsResponse {
    private String accessToken;
    private int expiresIn;
    private int refreshExpiresIn;
    private String tokenType;
    private int notBeforePolicy;
    private String scope;
}
