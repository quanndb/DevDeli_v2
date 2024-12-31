package com.example.identityService.application.service.impl;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.UserCommandMapper;
import com.example.identityService.application.service.UserCommandService;
import com.example.identityService.domain.User;
import com.example.identityService.domain.UserRole;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.command.RegisterCommand;
import com.example.identityService.domain.command.UpdateUserCommand;
import com.example.identityService.domain.command.UpdateUserInfoCommand;
import com.example.identityService.domain.repository.UserDomainRepository;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserCommandImpl implements UserCommandService {

    private final UserCommandMapper userCommandMapper;
    private final UserDomainRepository userDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;

    @Override
    public boolean register(RegisterRequest request) {
        boolean existedEmail = userDomainRepository.existsByEmail(request.getEmail());
        if (existedEmail) throw new AppExceptions(ErrorCode.EMAIL_EXISTED);

        RegisterCommand cmd = new RegisterCommand();
        userCommandMapper.fromCreateDtoToCreateCmd(cmd, request);
        User userDomain = new User(cmd);

        return userDomainRepository.save(userDomain)    ;
    }

    @Override
    public boolean createUser(CreateAccountRequest request) {
        boolean existedEmail = userDomainRepository.existsByEmail(request.getEmail());
        if (existedEmail) throw new AppExceptions(ErrorCode.EMAIL_EXISTED);
        boolean validRoleIds = userRoleDomainRepository.existsIds(request.getRoleIds());
        if(!validRoleIds) throw new AppExceptions(ErrorCode.ROLE_NOTFOUND);

        CreateUserCommand cmd = new CreateUserCommand();
        userCommandMapper.fromCreateDtoToCreateCmd(cmd, request);
        User userDomain = new User(cmd);

        return userDomainRepository.save(userDomain);
    }

    @Override
    public boolean updateUser(UUID userId, UpdateAccountRequest request) {
        boolean existedUser = userDomainRepository.existsById(userId);
        if (!existedUser) throw new AppExceptions(ErrorCode.NOTFOUND_EMAIL);
        boolean validRoleIds = userRoleDomainRepository.existsIds(request.getRoleIds());
        if(!validRoleIds) throw new AppExceptions(ErrorCode.ROLE_NOTFOUND);

        UpdateUserCommand cmd = new UpdateUserCommand();
        userCommandMapper.fromUpdateDtoToUpdateCmd(cmd, request);

        List<UserRole> userRoles = userRoleDomainRepository.getAllByUserId(userId);
        User foundDomain = userDomainRepository.getById(userId);
        foundDomain.updateUser(userRoles, cmd);

        return userDomainRepository.save(foundDomain);
    }

    @Override
    public boolean updateUserInfo(String email, UpdateProfileRequest request) {
        boolean existedUser = userDomainRepository.existsByEmail(email);
        if (!existedUser) throw new AppExceptions(ErrorCode.NOTFOUND_EMAIL);
        User foundDomain = userDomainRepository.getByEmail(email);

        UpdateUserInfoCommand cmd = new UpdateUserInfoCommand();
        userCommandMapper.fromUpdateDtoToUpdateCmd(cmd, request);
        foundDomain.updateUserInfo(cmd);
        return userDomainRepository.save(foundDomain);
    }

    @Override
    public boolean deleteUser(UUID userId) {
        boolean existedUser = userDomainRepository.existsById(userId);
        if (!existedUser) throw new AppExceptions(ErrorCode.NOTFOUND_EMAIL);
        User foundDomain = userDomainRepository.getById(userId);
        foundDomain.deleteUser();
        return userDomainRepository.save(foundDomain);
    }
}
