package com.example.identityService.service.impl;

import com.devdeli.common.service.TokenCacheService;
import com.example.identityService.service.auth.DefaultAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCacheImpl implements TokenCacheService {

    private final DefaultAuthService defaultAuthService;

    @Override
    public void invalidToken(String token) {

    }

    @Override
    public void invalidRefreshToken(String refreshToken) {

    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        defaultAuthService.logout(accessToken, refreshToken);
    }

    @Override
    public boolean isExisted(String token) {
        return defaultAuthService.introspect(token);
    }
}
