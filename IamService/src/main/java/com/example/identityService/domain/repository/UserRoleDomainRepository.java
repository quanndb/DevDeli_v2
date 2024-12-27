package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.UserRoleDomain;

import java.util.List;
import java.util.UUID;

public interface UserRoleDomainRepository extends DomainRepository<UserRoleDomain, UUID> {
    boolean existsIds(List<UUID> roleIds);

    List<UserRoleDomain> getAllByIds(List<UUID> roleIds);
}
