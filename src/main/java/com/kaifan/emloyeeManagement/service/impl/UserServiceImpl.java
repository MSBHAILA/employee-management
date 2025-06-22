package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.entity.User;
import com.kaifan.emloyeeManagement.repository.UserRepository;
import com.kaifan.emloyeeManagement.service.UserService;
import jakarta.transaction.Transactional;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    Keycloak keycloak;

    @Autowired
    UserRepository userRepository;

    @Value("${keycloak.realm}")
    private String realm;


    /*
    This method is used to sync users from keycloak to our database
    It is still in progress
     */
    @Override
    @Transactional
    public List<UserRepresentation> syncUsers() {
        List<UserRepresentation> users = keycloak.realm(realm).users().list();
        for (UserRepresentation kcUser : users) {
            // New user
            User newUser = new User();
            newUser.setUserId(kcUser.getId());
            newUser.setFirstName(kcUser.getFirstName());
            newUser.setLastName(kcUser.getLastName());
            newUser.setUsername(kcUser.getUsername());
            newUser.setStatus(kcUser.isEnabled());
            userRepository.save(newUser);

        }
        return users;
    }
}
