package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.RolePermission;

import java.util.List;
import java.util.UUID;

public interface RolePermissionDomainRepository extends DomainRepository<RolePermission, UUID> {
    List<RolePermission> findAllByRoleIdsIn(List<UUID> roleIds);
}
