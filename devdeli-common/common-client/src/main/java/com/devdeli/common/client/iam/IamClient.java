package com.devdeli.common.client.iam;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.config.FeignClientConfiguration;
import com.devdeli.common.dto.response.ApiResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        url = "${app.iam.internal-url}",
        name = "iam",
        contextId = "common-iam",
        configuration = FeignClientConfiguration.class,
        fallbackFactory = IamClientFallback.class)
public interface IamClient {
    @GetMapping("/api/v1.0.0/accounts/{email}/authorities")
    @LoadBalanced
    ApiResponse<UserAuthority> getUserAuthority(@PathVariable String email);
}
