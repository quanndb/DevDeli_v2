package com.example.identityService.application.service;

import com.devdeli.common.UserAuthority;
import com.example.identityService.domain.entity.Account;
import com.example.identityService.domain.entity.AccountRole;
import com.example.identityService.domain.entity.Role;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.domain.repository.AccountRepository;
import com.example.identityService.domain.repository.AccountRoleRepository;
import com.example.identityService.domain.repository.RolePermissionRepository;
import com.example.identityService.domain.repository.RoleRepository;
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
                .map(AccountRole::getRoleId)
                .toList();
        return roleRepository.findAllById(foundRoleIds).stream().map(Role::getName).toList();
    }

    public List<Role> getAllUserRoleId(String accountId) {
        List<String> foundRoleIds = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRole::getRoleId)
                .toList();
        return roleRepository.findAllById(foundRoleIds);
    }

    public boolean assignRolesForUser(String accountId, List<String> roles){
        accountRepository.findById(accountId).orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<String> accountRoleIdList = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRole::getRoleId)
                .toList();

        List<String> accountRoleNames = roleRepository.findAllById(accountRoleIdList)
                .stream()
                .map(Role::getName)
                .toList();

        List<AccountRole> saveAccountRoles = roleRepository.findAllByNameIn(roles.stream()
                        .filter(name -> !accountRoleNames.contains(name))
                        .toList())
                .stream()
                .map(item->AccountRole.builder()
                        .roleId(item.getId())
                        .accountId(accountId)
                        .build())
                .toList();

        accountRoleRepository.saveAll(saveAccountRoles);
        return true;
    }

    public boolean unassignRolesForUser(String accountId, List<String> roles){
        accountRepository.findById(accountId).orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));

        List<AccountRole> userRoles = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId);
        List<String> deleteRoleList = roleRepository.findAllByNameIn(roles.stream()
                        .toList()).stream()
                .map(Role::getId)
                .toList();

        List<AccountRole> deleteAccountRoleList = userRoles.stream()
                .filter(item -> deleteRoleList.contains(item.getRoleId()))
                .peek(item -> item.setDeleted(true))
                .toList();

        accountRoleRepository.saveAll(deleteAccountRoleList);
        return true;
    }

    public UserAuthority getAllUserAuthorities(String email) {
        Account found = accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<Role> roles = getAllUserRoleId(found.getId());
        boolean isRoot = roles.stream().anyMatch(Role::isRoot);
        if(isRoot) return new UserAuthority(email, true, List.of());
        List<String> authorities = rolePermissionRepository.getAllAuthoritiesInListRole(roles.stream()
                        .map(Role::getId)
                .collect(Collectors.toList()));
        return new UserAuthority(email, false, authorities);
    }
}
