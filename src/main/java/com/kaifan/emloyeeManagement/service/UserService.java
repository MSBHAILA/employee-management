package com.kaifan.emloyeeManagement.service;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {
    List<UserRepresentation> syncUsers();
}
