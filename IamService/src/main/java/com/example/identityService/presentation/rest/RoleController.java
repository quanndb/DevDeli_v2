package com.example.identityService.presentation.rest;

import com.example.identityService.application.DTO.ApiResponse;
import com.example.identityService.application.DTO.request.DetailsAssignPermissionRequest;
import com.example.identityService.application.DTO.request.CreateRoleRequest;
import com.example.identityService.application.DTO.request.RolePageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.response.RoleResponse;
import com.example.identityService.application.service.RolePermissionService;
import com.example.identityService.application.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RolePermissionService rolePermissionService;
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'roles.read')")
    public ApiResponse<PageResponse<RoleResponse>> getRoles(@ModelAttribute RolePageRequest request) {
        return ApiResponse.<PageResponse<RoleResponse>>builder()
                .code(200)
                .result(roleService.getRoles(request))
                .build();
    }

    @GetMapping("{roleId}")
    @PreAuthorize("hasPermission(null, 'roles.read')")
    public ApiResponse<List<String>> getAllPermissionOfRole(@PathVariable String roleId){
        return ApiResponse.<List<String>>builder()
                .code(200)
                .result(roleService.getAllRolePermission(roleId))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'roles.create')")
    public ApiResponse<String> addRole(@RequestBody @Valid CreateRoleRequest request){
        boolean result = roleService.createRole(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/{roleId}")
    @PreAuthorize("hasPermission(null, 'roles.update')")
    public ApiResponse<String> updateRole(@PathVariable String roleId, @RequestBody @Valid CreateRoleRequest request){
        boolean result = roleService.updateRole(roleId, request);
        return ApiResponse.<String>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasPermission(null, 'roles.delete')")
    public ApiResponse<String> deleteRole(@PathVariable String roleId){
        boolean result = roleService.deleteRole(roleId);
        return ApiResponse.<String>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasPermission(null, 'roles.create')")
    public ApiResponse<String> assignPermissionsForRole(@PathVariable String roleId,
                                                       @RequestBody List<DetailsAssignPermissionRequest> assignPermissionRequest){

        boolean result = rolePermissionService.assignPermission(roleId, assignPermissionRequest);
        return ApiResponse.<String>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasPermission(null, 'roles.delete')")
    public ApiResponse<String> unassignPermissionsForRole(@PathVariable String roleId,
                                                       @RequestBody List<DetailsAssignPermissionRequest> assignPermissionRequest){

        boolean result = rolePermissionService.unAssignPermission(roleId, assignPermissionRequest);
        return ApiResponse.<String>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }
}
