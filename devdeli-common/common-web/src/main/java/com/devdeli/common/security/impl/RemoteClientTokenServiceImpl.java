package com.devdeli.common.security.impl;

import com.devdeli.common.client.iam.IamClient;
import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;
import com.devdeli.common.service.ClientTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoteClientTokenServiceImpl implements ClientTokenService {

    private final IamClient iamClient;

    @Override
    public ClientTokenResponse getClientToken(ClientTokenRequest request) {
        return iamClient.getClientToken(request).getResult();
    }
}
