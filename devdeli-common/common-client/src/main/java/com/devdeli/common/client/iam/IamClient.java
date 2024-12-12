package com.devdeli.common.client.iam;

import com.devdeli.common.UserAuthority;
import com.devdeli.common.config.FeignClientConfiguration;
import com.devdeli.common.dto.response.Response;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        url = "${app.iam.internal-url:}",
        name = "iam",
        contextId = "common-iam",
        configuration = FeignClientConfiguration.class,
        fallbackFactory = IamClientFallback.class)
public interface IamClient {
    @GetMapping("/api/users/{userId}/authorities")
    @LoadBalanced
    Response<UserAuthority> getUserAuthority(@PathVariable UUID userId);

    @GetMapping("/api/users/{username}/authorities-by-username")
    @LoadBalanced
    Response<UserAuthority> getUserAuthority(@PathVariable String username);
}
