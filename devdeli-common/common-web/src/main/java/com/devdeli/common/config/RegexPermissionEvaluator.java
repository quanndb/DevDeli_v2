package com.devdeli.common.config;

import com.devdeli.common.UserAuthentication;
import com.devdeli.common.service.ResourceCheckerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class RegexPermissionEvaluator implements PermissionEvaluator {

    private final ResourceCheckerService resourceCheckerService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String requiredPermission = permission.toString();
        if (!(authentication instanceof UserAuthentication userAuthentication)) {
            // @TODO throw exception
            throw new RuntimeException("NOT_SUPPORTED_AUTHENTICATION");
        }

        if(Objects.equals(authentication.getName(), resourceCheckerService.ownerId(targetDomainObject.toString()))){
            return true;
        }

        if (userAuthentication.isRoot()) {
            return true;
        }

        return userAuthentication.getGrantedPermissions().stream()
                .anyMatch(p -> Pattern.matches(p, requiredPermission));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}
