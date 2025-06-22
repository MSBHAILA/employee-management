package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
