package com.example.identityService.infrastructure.domainRepository;

import com.example.identityService.domain.Role;
import com.example.identityService.domain.repository.RoleDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import com.example.identityService.infrastructure.persistence.mapper.RoleMapper;
import com.example.identityService.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RoleDomainRepositoryImpl implements RoleDomainRepository {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Role getById(UUID uuid) {
        return null;
    }

    @Override
    public List<Role> findAllByIds(List<UUID> uuids) {
        return List.of();
    }

    @Override
    public boolean save(Role domain) {
        return false;
    }

    @Override
    public boolean saveAll(List<Role> domains) {
        return false;
    }

    @Override
    public boolean delete(Role domain) {
        return false;
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return roleRepository.existsById(uuid);
    }

    @Override
    public List<Role> getAllInRoleIdsAndDeletedIsFalse(List<UUID> ids) {
        List<RoleEntity> roleEntities = roleRepository.findAllByIdInAndDeletedIsFalse(ids);
        return roleMapper.toListRoleDomain(roleEntities);
    }
}
