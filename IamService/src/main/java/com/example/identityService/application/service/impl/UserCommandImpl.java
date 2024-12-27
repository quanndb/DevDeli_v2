package com.example.identityService.application.service.impl;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.mapper.UserCommandMapper;
import com.example.identityService.application.service.UserCommandService;
import com.example.identityService.domain.UserDomain;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandImpl implements UserCommandService {

    private final UserCommandMapper userCommandMapper;
    private final UserDomainRepository userDomainRepository;

    @Override
    public boolean createUser(CreateAccountRequest request) {
        CreateUserCommand cmd = userCommandMapper.from(request);
        UserDomain userDomain = new UserDomain(cmd);

        userDomainRepository.save(userDomain);
        return true;
    }
}
