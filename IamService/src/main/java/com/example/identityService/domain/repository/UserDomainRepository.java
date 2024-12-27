package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.UserDomain;

import java.util.UUID;

public interface UserDomainRepository extends DomainRepository<UserDomain, UUID> {
}
