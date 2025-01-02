package com.example.identityService.presentation.rest;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.dto.response.PageResponse;
import com.devdeli.common.support.SecurityUtils;
import com.example.identityService.application.DTO.ApiResponse;
import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.SetUserEnableRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.service.AccountService;
import com.example.identityService.application.service.RolePermissionQueryService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final RolePermissionQueryService rolePermissionQueryService;


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
    public ApiResponse<Boolean> createAccount(@RequestBody @Valid CreateAccountRequest request){
        boolean result = userCommandService.createUser(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PutMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.update')")
    public ApiResponse<Boolean> updateAccount(@RequestBody @Valid UpdateAccountRequest request, @PathVariable UUID accountId){
        boolean result = userCommandService.updateUser(accountId, request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.read')")
    public ApiResponse<UserResponse> getAccountInfo(@PathVariable UUID accountId){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userQueryService.getUserInfo(accountId))
                .build();
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.delete')")
    public ApiResponse<Boolean> deleteAccount(@PathVariable UUID accountId){
        boolean result = userCommandService.deleteUser(accountId);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/{accountId}")
    @PreAuthorize("hasPermission(null, 'accounts.update')")
    public ApiResponse<Boolean> setEnableAccount(@RequestBody @Valid SetUserEnableRequest request, @PathVariable UUID accountId){
        boolean result = userCommandService.setUserEnable(accountId, request.isEnable());
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/users-import")
    @PreAuthorize("hasPermission(null, 'accounts.create')")
    public ApiResponse<List<String>> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> errors = userCommandService.importUsers(file, SecurityUtils.getCurrentUser()
                .orElseThrow(()->new AppExceptions(ErrorCode.UNAUTHENTICATED)));
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

    @GetMapping("/{email}/authorities")
    public ApiResponse<UserAuthority> getUserAuthoritiesByUserEmail(@PathVariable String email){
        UserAuthority result = rolePermissionQueryService.getUserAuthorityByUserEmail(email);
        return ApiResponse.<UserAuthority>builder()
                .code(200)
                .result(result)
                .build();
    }
}
