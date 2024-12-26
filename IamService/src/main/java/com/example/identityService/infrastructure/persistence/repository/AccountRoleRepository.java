package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleEntity,String> {
    List<AccountRoleEntity> findAllByAccountIdAndDeletedIsFalse(String accountId);

    boolean existsByAccountIdAndRoleId(String accountId, String roleId);
}
