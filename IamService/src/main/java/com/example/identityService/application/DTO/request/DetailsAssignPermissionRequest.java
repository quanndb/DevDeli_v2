package com.example.identityService.application.DTO.request;

import com.example.identityService.application.DTO.PermissionScope;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class DetailsAssignPermissionRequest {
    String permissionCode;
    @NotEmpty(message = "PERMISSION_NOT_EMPTY")
    List<PermissionScope> scopes;
}
