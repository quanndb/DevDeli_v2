package com.devdeli.common.security.impl;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.client.iam.IamClient;
import com.devdeli.common.security.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoteAuthorityServiceImpl implements AuthorityService {
    private final IamClient iamClient;

    public RemoteAuthorityServiceImpl(IamClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return null;
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
