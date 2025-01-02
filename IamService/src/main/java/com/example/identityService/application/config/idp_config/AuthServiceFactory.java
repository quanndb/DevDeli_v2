package com.example.identityService.application.config.idp_config;

import com.example.identityService.application.service.auth.AbstractAuthService;
import com.example.identityService.application.service.auth.DefaultAuthService;
import com.example.identityService.application.service.auth.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceFactory {

    private KeycloakService keycloakAuthService;
    private DefaultAuthService defaultAuthService;

    @Autowired
    public void setKeycloakAuthService(@Lazy KeycloakService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    @Autowired
    public void setDefaultAuthService(@Lazy DefaultAuthService defaultAuthService) {
        this.defaultAuthService = defaultAuthService;
    }

    @Value("${app.idp}")
    private IdpProvider idpProvider;

    public AbstractAuthService getAuthService() {
        switch (idpProvider) {
            case KEYCLOAK:
                return keycloakAuthService;
            default:
                return defaultAuthService;
        }
    }
}
