//package com.example.identityService.service.impl;
//
//import com.devdeli.common.dto.request.ClientTokenRequest;
//import com.devdeli.common.dto.response.ClientTokenResponse;
//import com.devdeli.common.service.ClientTokenService;
//import com.example.identityService.config.idp_config.AuthServiceFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//
//@Service
//@Primary
//@RequiredArgsConstructor
//public class ClientTokenServiceImpl implements ClientTokenService {
//
//    private final AuthServiceFactory authServiceFactory;
//    @Override
//    public ClientTokenResponse getClientToken(ClientTokenRequest request) {
//        return authServiceFactory.getAuthService().performGetClientToken(request);
//    }
//}
