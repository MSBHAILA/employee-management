package com.kaifan.emloyeeManagement.controller;

import com.kaifan.emloyeeManagement.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private UserService userService;

    @Value("${keycloak.realm}")
    private String realm;

    /*
    This endpoint is used to sync users from keycloak to our database
    It still needs improvement
     */
    @GetMapping("/sync-users")
    public ResponseEntity<List<UserRepresentation>> syncUsers() {
        return ResponseEntity.ok(userService.syncUsers());
    }
}
