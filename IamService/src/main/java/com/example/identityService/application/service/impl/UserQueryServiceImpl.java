package com.example.identityService.application.service.impl;

import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.config.idp_config.AuthServiceFactory;
import com.example.identityService.application.mapper.UserQueryMapper;
import com.example.identityService.application.service.AccountService;
import com.example.identityService.application.service.UserQueryService;
import com.example.identityService.domain.query.GetUserPageQuery;
import com.example.identityService.domain.query.LoginQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final AuthServiceFactory authServiceFactory;
    private final AccountService accountService;
    private final UserQueryMapper userQueryMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginQuery query = userQueryMapper.toLoginQuery(request);
        return authServiceFactory.getAuthService().login(query);
    }

    @Override
    public PageResponse<UserResponse> getUsers(UserPageRequest pageRequest) {
        GetUserPageQuery query = userQueryMapper.toUserPageQuery(pageRequest);
        return accountService.getUsers(query);
    }
}
