package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends AuditableDomain {
    private UUID id;
    private String name;
    private String code;
    private boolean deleted;
}
