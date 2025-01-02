package com.example.identityService.application.service;

import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import com.example.identityService.infrastructure.persistence.repository.AccountRoleRepository;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountRoleService {

    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    public List<String> getAllUserRole(UUID accountId) {
        List<UUID> foundRoleIds = accountRoleRepository.findAllByAccountIdAndDeletedIsFalse(accountId)
                .stream()
                .map(AccountRoleEntity::getRoleId)
                .toList();
        return roleRepository.findAllById(foundRoleIds).stream().map(RoleEntity::getName).toList();
    }
}
