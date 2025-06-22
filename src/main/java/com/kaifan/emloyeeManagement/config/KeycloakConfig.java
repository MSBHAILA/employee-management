package com.kaifan.emloyeeManagement.config;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        return Keycloak.getInstance(
                "http://localhost:8080",
                "master",
                "admin",
                "admin123",
                "my-public-client",
                "t0FuEP64CJQutatGCz3UVjwx7ZFCwR6N");
    }
}