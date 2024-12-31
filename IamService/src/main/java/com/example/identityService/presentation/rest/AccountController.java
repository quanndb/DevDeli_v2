package com.example.identityService.presentation.rest;

import com.devdeli.common.UserAuthority;
import com.example.identityService.application.DTO.ApiResponse;
import com.example.identityService.application.DTO.request.*;
import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.service.AccountRoleService;
import com.example.identityService.application.service.AccountService;
import com.example.identityService.application.service.UserCommandService;
import com.example.identityService.application.service.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRoleService accountRoleService;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ApiResponse<PageResponse<UserResponse>> getAccounts(@ParameterObject UserPageRequest request) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .result(userQueryService.getUsers(request))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'accounts.create')")
    public ApiResponse<?> createAccount(@RequestBody @Valid CreateAccountRequest request){
        boolean result = userCommandService.createUser(request);
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PutMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.update')")
    public ApiResponse<?> updateAccount(@RequestBody @Valid UpdateAccountRequest request, @PathVariable UUID accountId){
        boolean result = userCommandService.updateUser(accountId, request);
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ApiResponse<?> getAccount(@PathVariable UUID accountId){
        return ApiResponse.builder()
                .code(200)
                .result(accountService.getUserinfo(accountId))
                .build();
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.delete')")
    public ApiResponse<?> deleteAccount(@PathVariable UUID accountId){
        boolean result = userCommandService.deleteUser(accountId);
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.update')")
    public ApiResponse<?> setEnableAccount(@RequestBody @Valid SetUserEnableRequest request, @PathVariable UUID accountId){
        boolean result = accountService.setUserEnable(accountId, request.isEnable());
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/users-import")
    @PreAuthorize("hasPermission(null, 'accounts.create')")
    public ApiResponse<List<String>> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> errors = accountService.importUsers(file);
        return ApiResponse.<List<String>>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(errors.isEmpty()))
                .result(errors.isEmpty() ? null : errors)
                .build();
    }

    @GetMapping("/users-export")
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ResponseEntity<byte[]> exportUsers(@ParameterObject UserPageRequest request) throws IOException {
        byte[] result = accountService.exportUsers(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users_"+ LocalDateTime.now() +".xlsx");

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

//    roles
    @PostMapping("/{accountId}/roles")
    @PreAuthorize("hasPermission(null, 'roles.create')")
    public ApiResponse<?> assignRolesForUser(@RequestBody @Valid AssignRoleRequest request, @PathVariable UUID accountId){
        boolean result = accountRoleService.assignRolesForUser(accountId, request.getRoles());
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @DeleteMapping("/{accountId}/roles")
    @PreAuthorize("hasPermission(null, 'role.delete')")
    public ApiResponse<?> unassignRolesForUser(@RequestBody @Valid AssignRoleRequest request, @PathVariable UUID accountId){
        boolean result = accountRoleService.unassignRolesForUser(accountId, request.getRoles());
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/{accountId}/authorities")
    public ApiResponse<UserAuthority> getUserAuthorities(@PathVariable String accountId){
        UserAuthority result = accountRoleService.getAllUserAuthorities(accountId);
        return ApiResponse.<UserAuthority>builder()
                .code(200)
                .result(result)
                .build();
    }
}
