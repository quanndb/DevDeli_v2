package com.devdeli.common.security;

import com.devdeli.common.UserAuthentication;
import com.devdeli.common.UserAuthority;
import com.devdeli.common.service.AuthorityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final AuthorityService authorityService;

    public CustomAuthenticationFilter(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("CustomAuthenticationFilter");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) securityContext.getAuthentication();
        Jwt token = authentication.getToken();

        String clientId = authentication.getToken().getClaim("client_id");

        if(clientId != null) {
            User principal = new User(clientId, "", Set.of());
            AbstractAuthenticationToken auth =
                    new UserAuthentication(principal, token, null, true, false);

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
        else{
            Boolean isRoot = Boolean.FALSE;

            UserAuthority optionalUserAuthority = enrichAuthority(token);
            //@TODO enrich

            Set<SimpleGrantedAuthority> grantedPermissions = new HashSet<>();
            if(optionalUserAuthority != null){
                isRoot = optionalUserAuthority.getIsRoot();
                grantedPermissions = optionalUserAuthority.getGrantedPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
            }

            String username = token.getClaimAsString("email");
            User principal = new User(username, "", grantedPermissions);
            AbstractAuthenticationToken auth =
                    new UserAuthentication(principal, token, grantedPermissions, isRoot, !isRoot);

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        return !(authentication instanceof JwtAuthenticationToken);
    }

    private UserAuthority enrichAuthority(Jwt token) {
        // Call lấy UserAuthority từ IAM dựa vào AuthorityService lưu ý với service khác IAM thì impl sẽ là RemoteAuthorityServiceImpl,
        // IAM thì sẽ dùng AuthorityServiceImpl(@Primary)
        return authorityService.getUserAuthority(token.getClaimAsString("email"));
    }
}
