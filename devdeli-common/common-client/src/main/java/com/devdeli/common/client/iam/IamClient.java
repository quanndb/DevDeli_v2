package com.devdeli.common.client.iam;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.request.LogoutRequest;
import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.dto.response.ClientTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        url = "${app.iam.internal-url}",
        name = "iam",
        contextId = "common-iam")
public interface IamClient {
    @GetMapping("/api/v1.0.0/accounts/{email}/authorities")
    ApiResponse<UserAuthority> getUserAuthority(@PathVariable String email);

    @PostMapping("/api/v1.0.0/auth/client-token")
    ApiResponse<ClientTokenResponse> getClientToken(@RequestBody ClientTokenRequest request);

    @PostMapping("/api/v1.0.0/auth/logout")
    ApiResponse<Boolean> logout(@RequestBody LogoutRequest request, @RequestHeader(name = "Authorization") String token);

    @PostMapping("/api/v1.0.0/auth/introspect")
    ApiResponse<Boolean> introspect(@RequestHeader(name = "Authorization") String token);
}
