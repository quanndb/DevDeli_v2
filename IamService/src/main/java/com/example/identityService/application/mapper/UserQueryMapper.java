package com.example.identityService.application.mapper;

import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.domain.query.GetUserPageQuery;
import com.example.identityService.domain.query.LoginQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserQueryMapper {
    LoginQuery toLoginQuery(LoginRequest request);
    GetUserPageQuery toUserPageQuery(UserPageRequest request);
    UserPageRequest toUserPageQuery(GetUserPageQuery request);
}
