package com.devdeli.common.config;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.service.ClientTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private ClientTokenService clientTokenService;

    @Value("${app.iam.client-id}")
    private String CLIENT_ID;
    @Value("${app.iam.client-secret}")
    private String CLIENT_SECRET;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // @TODO header bearer token
        String token = clientTokenService.getClientToken(new ClientTokenRequest(CLIENT_ID, CLIENT_SECRET)).getAccessToken();
        requestTemplate.header("Authorization", "Bearer " + token);
    }
}
