package com.devdeli.common.security;

import com.devdeli.common.config.ActionLogFilter;
import com.devdeli.common.config.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity
@EnableFeignClients(basePackages = {"com.devdeli.common.client"})
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@RequiredArgsConstructor
public class HttpSecurityConfiguration {

    private final ActionLogFilter actionLogFilter;
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final ForbiddenTokenFilter forbiddenTokenFilter;
    private final JwtProperties jwtProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/health").permitAll()
                                .requestMatchers("auth/**").permitAll()
                                .requestMatchers("certificate/.well-known/jwks.json").permitAll()
                                .requestMatchers("public/**").permitAll()
                                .requestMatchers("authenticate/**").permitAll()
                                .requestMatchers("/api/**").authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(this.jwkResolver(this.jwtProperties)));
        http.addFilterAfter(this.forbiddenTokenFilter, BearerTokenAuthenticationFilter.class);
        http.addFilterAfter(this.customAuthenticationFilter, BearerTokenAuthenticationFilter.class);
        http.addFilterAfter(this.actionLogFilter, BearerTokenAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationManagerResolver<HttpServletRequest> jwkResolver(JwtProperties jwtProperties) {
        return new JwkAuthenticationManagerResolver(jwtProperties);
    }
}
