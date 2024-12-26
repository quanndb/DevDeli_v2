package com.devdeli.common.config;

import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignClientConfiguration {
    @Bean
    public FeignClientInterceptor requestInterceptor() {
        return new FeignClientInterceptor();
    }

    @Bean
    public Retryer feignRetryer() {
        // 1 second initial interval, 5 seconds max interval, 3 attempts
        return new Retryer.Default(1000, 5000, 3);
    }
}
