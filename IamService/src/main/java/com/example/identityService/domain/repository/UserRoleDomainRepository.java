package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserRoleDomainRepository extends DomainRepository<UserRole, UUID> {
    boolean existsIds(List<UUID> roleIds);
    List<UserRole> getAllByUserId(UUID userId);
    List<UserRole> getAllByUserIdAndDeletedIsFalse(UUID userId);
    List<UserRole> getAllByIds(List<UUID> roleIds);
}
