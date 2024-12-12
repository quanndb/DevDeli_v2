package com.example.storageservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    String[] PUBLIC_ENDPOINTS = {
            "public/files/*/download",
            "public/files/*/info",
            "swagger-ui/**",
            "api-docs/**",
            "v3/api-docs/**",
    };

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String AUTH_SERVER;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final ClientTokenFilter clientTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtConfigurer -> jwtConfigurer.decoder(JwtDecoders.fromIssuerLocation(AUTH_SERVER)))
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );

        http.addFilterAfter(clientTokenFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }
}
