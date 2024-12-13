package com.devdeli.common.security;

import com.devdeli.common.UserAuthority;

import java.util.UUID;

public interface AuthorityService {
    UserAuthority getUserAuthority(UUID userId);

    UserAuthority getUserAuthority(String email);

    UserAuthority getClientAuthority(UUID clientId);

}