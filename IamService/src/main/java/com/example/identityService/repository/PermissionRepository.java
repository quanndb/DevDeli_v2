package com.example.identityService.repository;

import com.example.identityService.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>, CustomPermissionRepository {
    Optional<Permission> findByCodeIgnoreCase(String name);
    List<Permission> findAllByCodeIgnoreCaseIn(List<String> codes);
}
