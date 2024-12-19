package com.devdeli.common.security.impl;

import com.devdeli.common.client.iam.IamClient;
import com.devdeli.common.dto.request.LogoutRequest;
import com.devdeli.common.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemoteTokenCacheServiceImpl implements TokenCacheService {

    private final IamClient iamClient;

    @Override
    public void invalidToken(String token) {

    }

    @Override
    public void invalidRefreshToken(String refreshToken) {

    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        iamClient.logout(new LogoutRequest(refreshToken), "Bearer "+accessToken);
    }

    @Override
    public boolean isExisted(String token) {
        return iamClient.introspect("Bearer "+token).getCode() != 200;
    }
}
