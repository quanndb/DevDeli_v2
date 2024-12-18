package com.devdeli.common.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClientTokenRequest {
    private final String grantType = "client_credentials";
    private String clientId;
    private String clientSecret;

    public ClientTokenRequest(String clientId, String clientSecret){
        this.clientSecret = clientSecret;
        this.clientId = clientId;
    }
}
