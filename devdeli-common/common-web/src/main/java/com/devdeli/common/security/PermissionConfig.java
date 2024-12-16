package com.devdeli.common.security;

import com.devdeli.common.config.RegexPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

@Configuration
public class PermissionConfig {
    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return new RegexPermissionEvaluator();
    }
    @Bean
    public MethodSecurityExpressionHandler expressionHandler(RegexPermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
