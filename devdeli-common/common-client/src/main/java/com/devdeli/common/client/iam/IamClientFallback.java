package com.devdeli.common.client.iam;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.request.LogoutRequest;
import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.dto.response.ClientTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class IamClientFallback implements IamClient {
    @Override
    public ApiResponse<UserAuthority> getUserAuthority(String email) {
        // Fallback logic here
        return ApiResponse.<UserAuthority>builder()
                .code(500)
                .message("Server error")
                .build();
    }

    @Override
    public ApiResponse<ClientTokenResponse> getClientToken(ClientTokenRequest request) {
        // Fallback logic here
        return ApiResponse.<ClientTokenResponse>builder()
                .code(500)
                .message("Server error")
                .build();
    }

    @Override
    public ApiResponse<Boolean> logout(LogoutRequest request, String token) {
        // Fallback logic here
        return ApiResponse.<Boolean>builder()
                .code(500)
                .message("Server error")
                .build();
    }

    @Override
    public ApiResponse<Boolean> introspect(String token) {
        // Fallback logic here
        return ApiResponse.<Boolean>builder()
                .code(500)
                .message("Server error")
                .build();
    }
} 