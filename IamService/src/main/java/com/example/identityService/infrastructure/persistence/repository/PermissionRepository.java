package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String>, CustomPermissionRepository {
    Optional<PermissionEntity> findByCodeIgnoreCase(String name);
    List<PermissionEntity> findAllByCodeIgnoreCaseIn(List<String> codes);
}
