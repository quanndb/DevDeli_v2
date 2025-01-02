package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleEntity,UUID> {
    List<AccountRoleEntity> findAllByAccountIdAndDeletedIsFalse(UUID accountId);
    List<AccountRoleEntity> findAllByAccountId(UUID accountId);
    boolean existsByAccountIdAndRoleId(UUID accountId, UUID roleId);
}
