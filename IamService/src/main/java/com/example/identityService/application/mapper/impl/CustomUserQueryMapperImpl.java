package com.example.identityService.application.mapper.impl;

import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.mapper.CustomUserQueryMapper;
import com.example.identityService.domain.Role;
import com.example.identityService.domain.User;
import com.example.identityService.domain.UserRole;
import com.example.identityService.domain.repository.RoleDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserQueryMapperImpl implements CustomUserQueryMapper {

    private final RoleDomainRepository roleDomainRepository;

    @Override
    public UserResponse from(User user) {
        List<String> roles = roleDomainRepository.getAllInRoleIdsAndDeletedIsFalse(user.getRoles().stream()
                        .map(UserRole::getRoleId)
                .collect(Collectors.toList()))
                .stream()
                .map(Role::getName)
                .toList();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullname(),
                user.getDob(),
                user.getYoe(),
                user.getAddress(),
                user.getGender(),
                user.getCloudImageUrl(),
                user.getCloudImageId(),
                user.getCreatedDate(),
                user.getCreatedBy(),
                user.isVerified(),
                user.isEnable(),
                user.isDeleted(),
                roles
        );
    }
}
