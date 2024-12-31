package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.User;

import java.util.UUID;

public interface UserDomainRepository extends DomainRepository<User, UUID> {
    boolean existsByEmail(String email);
    User getByEmail(String email);
}
