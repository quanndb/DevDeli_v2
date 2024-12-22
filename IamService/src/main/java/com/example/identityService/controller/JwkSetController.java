package com.example.identityService.controller;

import com.example.identityService.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class JwkSetController {
    private final TokenService tokenProvider;

    @GetMapping("/certificate/.well-known/jwks.json")
    Map<String, Object> keys() {
        return this.tokenProvider.jwkSet().toJSONObject();
    }
}