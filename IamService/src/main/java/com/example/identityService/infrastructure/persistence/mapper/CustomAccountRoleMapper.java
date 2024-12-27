package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.domain.UserRoleDomain;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;

import java.util.List;

public interface CustomAccountRoleMapper {
    UserRoleDomain toUserRoleDomain(AccountRoleEntity request);
    AccountRoleEntity toAccountRoleEntity(UserRoleDomain request);
    List<UserRoleDomain> toUserRoleDomainList(List<AccountRoleEntity> request);
    List<AccountRoleEntity> toAccountRoleEntityList(List<UserRoleDomain> request);
}
