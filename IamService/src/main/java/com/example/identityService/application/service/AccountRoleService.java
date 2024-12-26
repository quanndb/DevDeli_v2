package com.example.identityService.application.service;

import com.devdeli.common.UserAuthority;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import com.example.identityService.infrastructure.persistence.repository.AccountRoleRepository;
import com.example.identityService.infrastructure.persistence.repository.RolePermissionRepository;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountRoleService {

    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRepository accountRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public List<String> getAllUserRole(String accountId) {
        List<String> foundRoleIds = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRoleEntity::getRoleId)
                .toList();
        return roleRepository.findAllById(foundRoleIds).stream().map(RoleEntity::getName).toList();
    }

    public List<RoleEntity> getAllUserRoleId(String accountId) {
        List<String> foundRoleIds = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRoleEntity::getRoleId)
                .toList();
        return roleRepository.findAllById(foundRoleIds);
    }

    public boolean assignRolesForUser(String accountId, List<String> roles){
        accountRepository.findById(accountId).orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<String> accountRoleIdList = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRoleEntity::getRoleId)
                .toList();

        List<String> accountRoleNames = roleRepository.findAllById(accountRoleIdList)
                .stream()
                .map(RoleEntity::getName)
                .toList();

        List<AccountRoleEntity> saveAccountRoles = roleRepository.findAllByNameIn(roles.stream()
                        .filter(name -> !accountRoleNames.contains(name))
                        .toList())
                .stream()
                .map(item-> AccountRoleEntity.builder()
                        .roleId(item.getId())
                        .accountId(accountId)
                        .build())
                .toList();

        accountRoleRepository.saveAll(saveAccountRoles);
        return true;
    }

    public boolean unassignRolesForUser(String accountId, List<String> roles){
        accountRepository.findById(accountId).orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));

        List<AccountRoleEntity> userRoles = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId);
        List<String> deleteRoleList = roleRepository.findAllByNameIn(roles.stream()
                        .toList()).stream()
                .map(RoleEntity::getId)
                .toList();

        List<AccountRoleEntity> deleteAccountRoleList = userRoles.stream()
                .filter(item -> deleteRoleList.contains(item.getRoleId()))
                .peek(item -> item.setDeleted(true))
                .toList();

        accountRoleRepository.saveAll(deleteAccountRoleList);
        return true;
    }

    public UserAuthority getAllUserAuthorities(String email) {
        AccountEntity found = accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<RoleEntity> roles = getAllUserRoleId(found.getId());
        boolean isRoot = roles.stream().anyMatch(RoleEntity::isRoot);
        if(isRoot) return new UserAuthority(email, true, List.of());
        List<String> authorities = rolePermissionRepository.getAllAuthoritiesInListRole(roles.stream()
                        .map(RoleEntity::getId)
                .collect(Collectors.toList()));
        return new UserAuthority(email, false, authorities);
    }
}
