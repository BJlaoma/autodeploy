package com.autodeploy.demo.mapper;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autodeploy.demo.entity.User;

public interface UserMapper extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUuid(String uuid);
} 