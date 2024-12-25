package com.example.identityService.domain.repository;

import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.UserResponse;

import java.util.List;

public interface CustomAccountRepository {
    List<UserResponse> search(UserPageRequest request);
    Long count(UserPageRequest request);
}
