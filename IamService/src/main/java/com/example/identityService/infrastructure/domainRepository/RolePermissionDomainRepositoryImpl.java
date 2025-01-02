package com.example.identityService.infrastructure.domainRepository;

import com.example.identityService.domain.RolePermission;
import com.example.identityService.domain.repository.RolePermissionDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.RolePermissionEntity;
import com.example.identityService.infrastructure.persistence.mapper.RolePermissionMapper;
import com.example.identityService.infrastructure.persistence.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RolePermissionDomainRepositoryImpl implements RolePermissionDomainRepository {

    private final RolePermissionRepository rolePermissionRepository;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RolePermission> findAllByRoleIdsIn(List<UUID> roleIds) {
        List<RolePermissionEntity> rolePermissionEntityList = rolePermissionRepository
                .findAllByRoleIdInAndDeletedIsFalse(roleIds);
        return rolePermissionMapper.toListRolePermissionDomain(rolePermissionEntityList);
    }

    @Override
    public Optional<RolePermission> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public RolePermission getById(UUID uuid) {
        return null;
    }

    @Override
    public List<RolePermission> findAllByIds(List<UUID> uuids) {
        return List.of();
    }

    @Override
    public boolean save(RolePermission domain) {
        return false;
    }

    @Override
    public boolean saveAll(List<RolePermission> domains) {
        return false;
    }

    @Override
    public boolean delete(RolePermission domain) {
        return false;
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }
}
