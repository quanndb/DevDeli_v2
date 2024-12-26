package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String>, CustomRoleRepository {
    Optional<RoleEntity> findByNameIgnoreCase(String roleName);
    List<RoleEntity> findAllByNameIn(List<String> names);
}
