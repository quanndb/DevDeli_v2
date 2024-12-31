package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.domain.UserRole;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountRoleMapper{
    UserRole toUserRoleDomain(AccountRoleEntity request);
    AccountRoleEntity toAccountRoleEntity(UserRole request);
    List<UserRole> toUserRoleDomainList(List<AccountRoleEntity> request);
    List<AccountRoleEntity> toAccountRoleEntityList(List<UserRole> request);
}
