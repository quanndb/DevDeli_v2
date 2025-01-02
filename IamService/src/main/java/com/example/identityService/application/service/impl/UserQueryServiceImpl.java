package com.example.identityService.application.service.impl;

import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.config.idp_config.AuthServiceFactory;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.CustomUserQueryMapper;
import com.example.identityService.application.service.AccountService;
import com.example.identityService.application.service.UserQueryService;
import com.example.identityService.domain.User;
import com.example.identityService.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final AccountService accountService;
    private final AuthServiceFactory authServiceFactory;
    private final UserDomainRepository userDomainRepository;
    private final CustomUserQueryMapper customUserQueryMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        return authServiceFactory.getAuthService().login(request);
    }

    @Override
    public PageResponse<UserResponse> getUsers(UserPageRequest pageRequest) {
        return accountService.getUsers(pageRequest);
    }

    @Override
    public UserResponse getUserInfo(UUID userId) {
        User foundDomain = userDomainRepository.findById(userId)
                .orElseThrow(()->new AppExceptions(ErrorCode.ACCOUNT_NOTFOUND));
        return customUserQueryMapper.from(foundDomain);
    }
}
