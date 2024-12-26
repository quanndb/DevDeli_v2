package com.example.identityService.infrastructure.persistence.entity;

import com.devdeli.common.Auditable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@jakarta.persistence.Entity
@Getter
@Setter
@Builder
@Table(name = "account_role")
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountRoleEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String roleId;
    @Column(nullable = false)
    private boolean deleted;
}
