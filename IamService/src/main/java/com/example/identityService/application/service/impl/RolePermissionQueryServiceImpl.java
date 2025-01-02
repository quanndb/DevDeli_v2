package com.example.identityService.application.service.impl;

import com.devdeli.common.UserAuthority;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.service.RolePermissionQueryService;
import com.example.identityService.domain.RolePermission;
import com.example.identityService.domain.UserRole;
import com.example.identityService.domain.repository.RolePermissionDomainRepository;
import com.example.identityService.domain.repository.UserDomainRepository;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import com.example.identityService.infrastructure.persistence.repository.AccountRoleRepository;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePermissionQueryServiceImpl implements RolePermissionQueryService {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final UserDomainRepository userDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final RolePermissionDomainRepository rolePermissionDomainRepository;

    @Override
    public UserAuthority getUserAuthorityByUserId(UUID userId) {
        boolean isExisted = userDomainRepository.existsById(userId);
        if(!isExisted) throw new AppExceptions(ErrorCode.ACCOUNT_NOTFOUND);
        List<AccountRoleEntity> accountRoleEntities = accountRoleRepository.findAllByAccountId(userId);
        List<RoleEntity> roleEntities = roleRepository.findAllByIdInAndDeletedIsFalse(accountRoleEntities
                .stream().map(AccountRoleEntity::getRoleId).toList());
        for (RoleEntity roleEntity : roleEntities) {
            if(roleEntity.isRoot()) return new UserAuthority(userId, true, List.of());
        }
        List<String> authorities = getAllRolePermissionsOfUser(userId);
        return new UserAuthority(userId, false, authorities);
    }

    @Override
    public UserAuthority getUserAuthorityByUserEmail(String email) {
        AccountEntity accountEntity = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppExceptions(ErrorCode.ACCOUNT_NOTFOUND));
        return getUserAuthorityByUserId(accountEntity.getId());
    }

    @Override
    public List<String> getAllRolePermissionsOfUser(UUID userId) {
        boolean isExisted = userDomainRepository.existsById(userId);
        if(!isExisted) throw new AppExceptions(ErrorCode.ACCOUNT_NOTFOUND);

        List<UserRole> userRoles = userRoleDomainRepository.getAllByUserId(userId);
        return getAllRolePermissionsInList(userRoles.stream().map(UserRole::getRoleId).toList());
    }

    @Override
    public List<String> getAllRolePermissionsOfRole(UUID roleId) {
        return getAllRolePermissionsInList(List.of(roleId));
    }

    @Override
    public List<String> getAllRolePermissionsInList(List<UUID> roleIds) {
        List<RolePermission> rolePermissions = rolePermissionDomainRepository.findAllByRoleIdsIn(roleIds);

        return rolePermissions.stream()
                .map(item -> item.getPermissionCode().toLowerCase()
                        +"."
                        + item.getScope().toString().toLowerCase())
                .toList();
    }
}
