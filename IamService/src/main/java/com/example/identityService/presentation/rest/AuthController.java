package com.example.identityService.presentation.rest;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;
import com.devdeli.common.support.SecurityUtils;
import com.example.identityService.application.DTO.ApiResponse;
import com.example.identityService.application.DTO.request.ChangePasswordRequest;
import com.example.identityService.application.DTO.request.ForgotPasswordRequest;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.devdeli.common.dto.request.LogoutRequest;
import com.example.identityService.application.DTO.request.RefreshTokenRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.ResetPasswordRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.service.UserCommandService;
import com.example.identityService.application.service.UserQueryService;
import com.example.identityService.application.util.IpChecker;
import com.example.identityService.application.util.JsonMapper;
import com.example.identityService.application.util.ObjectValidator;
import com.example.identityService.application.config.idp_config.AuthServiceFactory;
import com.example.identityService.application.service.auth.AbstractAuthService;
import com.example.identityService.application.service.auth.DefaultAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceFactory authServiceFactory;
    private final DefaultAuthService authService;
    private final ObjectValidator objectValidator;
    private final JsonMapper jsonMapper;
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest dto) {
        var res = userQueryService.login(dto);
        return ApiResponse.<LoginResponse>builder()
                .code(200)
                .result(res)
                .build();
    }

    @PostMapping("/client-token")
    public ApiResponse<ClientTokenResponse> getClientToken(@RequestBody ClientTokenRequest clientTokenRequest) {
        ClientTokenResponse result = authServiceFactory.getAuthService().performGetClientToken(clientTokenRequest);
        return ApiResponse.<ClientTokenResponse>builder()
                .code(200)
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest requestHeader, @RequestBody @Valid LogoutRequest requestBody) throws ParseException {
        String accessToken = requestHeader.getHeader("Authorization").substring(7);
        String refreshToken = requestBody.getRefreshToken();
        boolean result = authService.logout(accessToken, refreshToken);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/introspect")
    public ApiResponse<Boolean> introspect(HttpServletRequest requestHeader) {
        String token = requestHeader.getHeader("Authorization").substring(7);
        boolean result = authService.introspect(token);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .result(result)
                .build();
    }

    @PostMapping("/registration")
    public ApiResponse<Boolean> register(@RequestBody @Valid RegisterRequest dto) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("Please check your verification email")
                .result(userCommandService.register(dto))
                .build();
    }

    @GetMapping("/refresh-token")
    public ApiResponse<?> getNewAccessToken(@RequestBody @Valid RefreshTokenRequest requestBody) {
        String refreshToken = requestBody.getRefreshToken();
        return ApiResponse.builder()
                .code(200)
                .result(authServiceFactory.getAuthService().getNewToken(refreshToken))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<?> getProfile() {
        return ApiResponse.builder()
                .code(200)
                .result(authService.getProfile())
                .build();
    }

    @GetMapping("/verification")
    public ApiResponse<Object> verifyEmailAndIP(@RequestParam String token) {
        return ApiResponse.builder()
                .code(200)
                .result(authService.verifyEmailAndIP(token))
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Boolean> fogotPasswordAttemp(@RequestBody @Valid ForgotPasswordRequest dto) {
        boolean result = authService.forgotPassword(dto.getEmail());
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Boolean> resetPassword(@RequestBody @Valid ResetPasswordRequest passwordRequestDTO) throws ParseException {
        boolean result = AbstractAuthService.resetPassword(passwordRequestDTO);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PatchMapping("/me")
    public ApiResponse<Boolean> updateProfile(@RequestBody @Valid UpdateProfileRequest request) throws IOException {
        boolean result = userCommandService
                .updateUserInfo(SecurityUtils.getCurrentUser().orElseThrow(()->new AppExceptions(ErrorCode.UNAUTHENTICATED)), request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @PutMapping("/me/change-password")
    public ApiResponse<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordDTO) {
        boolean result = AbstractAuthService.changePassword(changePasswordDTO);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(result))
                .build();
    }

    @GetMapping("/google")
    public ApiResponse<LoginResponse> loginWithGoogle(@RequestParam String code) {
        return ApiResponse.<LoginResponse>builder()
                .code(200)
                .result(authServiceFactory.getAuthService().loginWithGoogle(code))
                .build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.internalServerError()
                .build();
    }
}
