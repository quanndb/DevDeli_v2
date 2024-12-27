package com.example.identityService.infrastructure.persistence.mapper.impl;

import com.example.identityService.domain.UserRoleDomain;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import com.example.identityService.infrastructure.persistence.mapper.CustomAccountRoleMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAccountRoleMapperImpl implements CustomAccountRoleMapper {
    @Override
    public UserRoleDomain toUserRoleDomain(AccountRoleEntity request) {
        if (request == null) {
            return null;
        }
        UserRoleDomain userRoleDomain = new UserRoleDomain();
        userRoleDomain.setId(request.getId());
        userRoleDomain.setUserId(request.getAccountId());
        userRoleDomain.setRoleId(request.getRoleId());
        userRoleDomain.setDeleted(request.isDeleted());
        return userRoleDomain;
    }

    @Override
    public AccountRoleEntity toAccountRoleEntity(UserRoleDomain request) {
        if (request == null) {
            return null;
        }
        AccountRoleEntity accountRoleEntity = new AccountRoleEntity();
        accountRoleEntity.setId(request.getId());
        accountRoleEntity.setAccountId(request.getUserId());
        accountRoleEntity.setRoleId(request.getRoleId());
        accountRoleEntity.setDeleted(request.isDeleted());
        return accountRoleEntity;
    }

    @Override
    public List<UserRoleDomain> toUserRoleDomainList(List<AccountRoleEntity> request) {
        if(request == null) return List.of();
        List<UserRoleDomain> userRoleDomainList = new ArrayList<>();
        for(AccountRoleEntity entity : request){
            UserRoleDomain userRoleDomain = toUserRoleDomain(entity);
            if(userRoleDomain != null){
                userRoleDomainList.add(userRoleDomain);
            }
        }
        return userRoleDomainList;
    }

    @Override
    public List<AccountRoleEntity> toAccountRoleEntityList(List<UserRoleDomain> request) {
        if(request == null) return List.of();
        List<AccountRoleEntity> accountRoleEntityList = new ArrayList<>();
        for(UserRoleDomain domain : request){
            AccountRoleEntity accountRoleEntity = toAccountRoleEntity(domain);
            if(accountRoleEntity != null){
                accountRoleEntityList.add(accountRoleEntity);
            }
        }
        return accountRoleEntityList;
    }
}
