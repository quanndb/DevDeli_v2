package com.example.identityService.service;

import com.example.identityService.DTO.response.KeyCloakClientCredentialsResponse;
import com.example.identityService.repository.client.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    @Value("${keycloak.auth-server-url}")
    private String KEYCLOAK_AUTH_URL;
    @Value("${storage.client.client-id}")
    private String CLIENT_ID;
    @Value("${storage.client.client-secret}")
    private String CLIENT_SECRET;

    private final StorageClient storageClient;


    public boolean uploadPrivateFiles(List<MultipartFile> files, String ownerId) {
        String token = login().getAccessToken();
        var res = storageClient.upPrivateFile(files, ownerId, "Bear "+token);

        return res != null;
    }

    public KeyCloakClientCredentialsResponse login() {
        String body = String.format("client_id=%s&client_secret=%s&grant_type=%s",
                CLIENT_ID, CLIENT_SECRET, "client_credentials");

        var res = WebClient.create(KEYCLOAK_AUTH_URL)
                .post()
                .uri("/realms/IAM2/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(KeyCloakClientCredentialsResponse.class).block();
        return res;
    }
}
