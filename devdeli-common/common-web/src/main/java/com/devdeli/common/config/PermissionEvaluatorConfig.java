package com.devdeli.common.config;

import com.devdeli.common.service.ResourceCheckerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

@Configuration
@RequiredArgsConstructor
public class PermissionEvaluatorConfig {

    private final ResourceCheckerService resourceCheckerService;

    @Bean
    public RegexPermissionEvaluator regexPermissionEvaluator() {
        return new RegexPermissionEvaluator(resourceCheckerService);
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RegexPermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
