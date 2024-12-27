package com.example.identityService.application.mapper.impl;

import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.CustomUserCommandMapper;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// for complicated case
@Component
@RequiredArgsConstructor
public class CustomUserCommandMapperImpl implements CustomUserCommandMapper {

    UserRoleDomainRepository userRoleDomainRepository;

    @Override
    public CreateUserCommand from(CreateAccountRequest request) {
        if ( request == null ) {
            return null;
        }
        // check all valid roleId
        boolean valid = userRoleDomainRepository.existsIds(request.getRoleIds());
        if(!valid) throw new AppExceptions(ErrorCode.ROLE_NOTFOUND);
        // domain data
        CreateUserCommand createUserCommand = new CreateUserCommand();

        createUserCommand.setEmail( request.getEmail() );
        createUserCommand.setPassword( request.getPassword() );
        createUserCommand.setFullname( request.getFullname() );
        createUserCommand.setDob( request.getDob() );
        createUserCommand.setYoe( request.getYoe() );
        createUserCommand.setVerified( request.getVerified() );
        createUserCommand.setEnable( request.getEnable() );
        createUserCommand.setGender( request.getGender() );
        createUserCommand.setAddress( request.getAddress() );
        createUserCommand.setCloudImageUrl( request.getCloudImageUrl() );
        createUserCommand.setCloudImageId( request.getCloudImageId() );
        createUserCommand.setIp( request.getIp() );
        createUserCommand.setRoleIds( request.getRoleIds() );
        return createUserCommand;
    }
}
