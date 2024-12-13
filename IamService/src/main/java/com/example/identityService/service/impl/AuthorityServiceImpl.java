package com.example.identityService.service.impl;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.security.AuthorityService;
import com.example.identityService.service.AccountRoleService;
import com.example.identityService.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AccountRoleService accountRoleService;

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String email) {
        return accountRoleService.getAllUserAuthorities(email);
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
