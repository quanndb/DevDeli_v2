package com.example.identityService.infrastructure.domainRepository;

import com.example.identityService.domain.UserRoleDomain;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.AccountRoleEntity;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import com.example.identityService.infrastructure.persistence.mapper.AccountRoleMapper;
import com.example.identityService.infrastructure.persistence.mapper.CustomAccountRoleMapper;
import com.example.identityService.infrastructure.persistence.mapper.impl.CustomAccountRoleMapperImpl;
import com.example.identityService.infrastructure.persistence.repository.AccountRoleRepository;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoleDomainRepositoryImpl implements UserRoleDomainRepository {

    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final CustomAccountRoleMapperImpl accountRoleMapper;

    @Override
    public boolean existsIds(List<UUID> roleIds) {
        return roleRepository.validIds(roleIds);
    }

    @Override
    public List<UserRoleDomain> getAllByIds(List<UUID> roleIds) {
        return List.of();
    }

    @Override
    public Optional<UserRoleDomain> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public UserRoleDomain getById(UUID uuid) {
        return null;
    }

    @Override
    public List<UserRoleDomain> findAllByIds(List<UUID> uuids) {
        return List.of();
    }

    @Override
    public boolean save(UserRoleDomain domain) {
        return true;
    }

    @Override
    public boolean saveAll(List<UserRoleDomain> domains) {
        List<AccountRoleEntity> accountRoleEntityList = accountRoleMapper
                .toAccountRoleEntityList(domains);
        accountRoleRepository.saveAll(accountRoleEntityList);
        return true;
    }

    @Override
    public boolean delete(UserRoleDomain domain) {
        return true;
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return true;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return true;
    }
}
