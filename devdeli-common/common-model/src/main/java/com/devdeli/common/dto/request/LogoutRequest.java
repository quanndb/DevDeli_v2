package com.devdeli.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutRequest {
    private String refreshToken;
}
