package com.example.identityService.infrastructure.persistence.entity;

import com.devdeli.common.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "role")
@AllArgsConstructor
@RequiredArgsConstructor
public class RoleEntity extends Auditable {
    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private boolean deleted;
    @Column(nullable = false)
    private boolean root;
}
