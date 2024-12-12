package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.DTO.request.AssignRoleRequest;
import com.example.identityService.DTO.request.CreateAccountRequest;
import com.example.identityService.DTO.request.SetUserEnableRequest;
import com.example.identityService.DTO.request.UserPageRequest;
import com.example.identityService.DTO.response.PageResponse;
import com.example.identityService.DTO.response.UserResponse;
import com.example.identityService.service.AccountRoleService;
import com.example.identityService.service.AccountService;
import com.example.identityService.service.auth.AbstractAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRoleService accountRoleService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ApiResponse<PageResponse<UserResponse>> getAccounts(@ParameterObject UserPageRequest request) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .result(accountService.getUsers(request))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'accounts.create')")
    public ApiResponse<?> createAccount(@RequestBody @Valid CreateAccountRequest request){
        boolean result = AbstractAuthService.createUser(request);
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ApiResponse<?> getAccount(@PathVariable String accountId){
        return ApiResponse.builder()
                .code(200)
                .result(accountService.getUserinfo(accountId))
                .build();
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.delete')")
    public ApiResponse<?> deleteAccount(@PathVariable String accountId){
        boolean result = accountService.deleteUser(accountId);
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.update')")
    public ApiResponse<?> setEnableAccount(@RequestBody @Valid SetUserEnableRequest request, @PathVariable String accountId){
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
    public ApiResponse<?> assignRolesForUser(@RequestBody @Valid AssignRoleRequest request, @PathVariable String accountId){
        boolean result = accountRoleService.assignRolesForUser(accountId, request.getRoles());
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @DeleteMapping("/{accountId}/roles")
    @PreAuthorize("hasPermission(null, 'role.delete')")
    public ApiResponse<?> unassignRolesForUser(@RequestBody @Valid AssignRoleRequest request, @PathVariable String accountId){
        boolean result = accountRoleService.unassignRolesForUser(accountId, request.getRoles());
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }


}
