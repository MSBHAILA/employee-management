package com.kaifan.emloyeeManagement.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static Long getCurrentUserId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User not authenticated");
        }

        // Assuming JWT contains userId
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        return Long.valueOf(jwtAuth.getToken().getClaimAsString("userId"));
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    public static String getCurrentEmployeeId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User not authenticated");
        }

        // Assuming JWT contains userId
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        return jwtAuth.getToken().getClaimAsString("employeeId");
    }
}
