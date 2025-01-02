package com.example.identityService.application.DTO.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private boolean deleted;
}
