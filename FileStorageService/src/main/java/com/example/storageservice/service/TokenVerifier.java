package com.example.storageservice.service;

import com.example.storageservice.exception.AppExceptions;
import com.example.storageservice.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class TokenVerifier {

    private JwtDecoder jwtDecoder;

    @Value(value = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String AUTH_SERVER;

    @PostConstruct
    public void init() {
        this.jwtDecoder = JwtDecoders.fromIssuerLocation(AUTH_SERVER);
    }

    public Jwt verifyToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException ex) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }
    }
}
