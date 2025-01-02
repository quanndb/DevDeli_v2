package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.Role;

import java.util.List;
import java.util.UUID;

public interface RoleDomainRepository extends DomainRepository<Role, UUID> {
    List<Role> getAllInRoleIdsAndDeletedIsFalse(List<UUID> ids);
}
