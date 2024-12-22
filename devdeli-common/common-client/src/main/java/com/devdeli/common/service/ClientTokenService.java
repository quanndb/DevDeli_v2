package com.devdeli.common.service;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;

public interface ClientTokenService {
    ClientTokenResponse getClientToken(ClientTokenRequest request);
}
