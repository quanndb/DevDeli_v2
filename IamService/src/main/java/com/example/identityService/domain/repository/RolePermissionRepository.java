package com.example.identityService.domain.repository;

import com.example.identityService.domain.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findAllByRoleIdAndDeletedIsFalse(String roleId);

    @Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM role_permission r " +
            "WHERE r.role_id IN :roleIds AND r.permission_code = :permissionId AND r.scope = :scope AND r.deleted = FALSE", nativeQuery = true)
    boolean isValidPermissionScope(List<String> roleIds, String permissionId, String scope);

    @Query(value = "SELECT DISTINCT CONCAT(LOWER(rp.permission_code), '.', LOWER(rp.scope)) as authority " +
            "FROM role_permission rp WHERE rp.role_id IN :roleIds AND rp.deleted = false", nativeQuery = true)
    List<String> getAllAuthoritiesInListRole(List<String> roleIds);
}
