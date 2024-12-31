package com.example.identityService.domain;

import com.devdeli.common.AuditableDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditableDomain {
    private String id;
    private String name;
    private String description;
    private boolean deleted;
    private boolean root;
}
