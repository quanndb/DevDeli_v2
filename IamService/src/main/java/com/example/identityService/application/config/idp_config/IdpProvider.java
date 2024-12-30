package com.example.identityService.application.config.idp_config;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IdpProvider {
    KEYCLOAK("KEYCLOAK"),
    DEFAULT("DEFAULT");

    private final String name;
}
