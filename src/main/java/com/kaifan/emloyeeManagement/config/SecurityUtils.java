package com.kaifan.emloyeeManagement.config;

import com.kaifan.emloyeeManagement.entity.Employee;
import com.kaifan.emloyeeManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Long getCurrentUserId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User not authenticated");
        }

        // Assuming JWT contains userId
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        return Long.valueOf(jwtAuth.getToken().getClaimAsString("userId"));
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    public String getCurrentEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication instanceof JwtAuthenticationToken)) {
            throw new SecurityException("User not authenticated or invalid authentication type");
        }

        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String preferredUsername = jwtAuth.getToken().getClaimAsString("preferred_username");

        if (preferredUsername == null) {
            throw new SecurityException("preferred_username claim not found in token");
        }

        return employeeRepository.findByAdUsername(preferredUsername)
                .map(Employee::getId)
                .orElseThrow(() -> new SecurityException("No employee found with AD username: " + preferredUsername));
    }

    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication instanceof JwtAuthenticationToken)) {
            throw new SecurityException("User not authenticated or invalid authentication type");
        }

        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String preferredUsername = jwtAuth.getToken().getClaimAsString("preferred_username");

        if (preferredUsername == null) {
            throw new SecurityException("preferred_username claim not found in token");
        }

        return employeeRepository.findByAdUsername(preferredUsername)
                .orElseThrow(() -> new SecurityException("No employee found with AD username: " + preferredUsername));
    }
}

