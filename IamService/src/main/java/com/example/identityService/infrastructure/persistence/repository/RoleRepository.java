package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID>, CustomRoleRepository {
    Optional<RoleEntity> findByNameIgnoreCaseAndDeletedIsFalse(String roleName);
    List<RoleEntity> findAllByNameInAndDeletedIsFalse(List<UUID> names);
    List<RoleEntity> findAllByIdInAndDeletedIsFalse(List<UUID> ids);
    @Query("SELECT COUNT(r.id) = :#{#ids.size()} FROM RoleEntity r WHERE r.id IN :ids AND r.deleted = false")
    boolean validIds(@Param("ids") List<UUID> ids);
}
