package com.devdeli.common.security.impl;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.client.iam.IamClient;
import com.devdeli.common.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoteAuthorityServiceImpl implements AuthorityService {
    private final IamClient iamClient;

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return iamClient.getUserAuthority(userId).getResult();
    }

    @Override
    public UserAuthority getUserAuthority(String email) {
        return iamClient.getUserAuthority(email).getResult();
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
