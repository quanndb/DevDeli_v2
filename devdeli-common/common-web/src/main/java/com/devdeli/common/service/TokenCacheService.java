package com.devdeli.common.service;

public interface TokenCacheService {

//    String INVALID_REFRESH_TOKEN_CACHE = "invalid-refresh-token";
//    String INVALID_TOKEN_CACHE = "invalid-access-token";

    void invalidToken(String token);

    void invalidRefreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    boolean isExisted(String token);

    default boolean isInvalidToken(String token) {
        return isExisted(token);
    }

    default boolean isInvalidRefreshToken(String refreshToken) {
        return isExisted(refreshToken);
    }
}
