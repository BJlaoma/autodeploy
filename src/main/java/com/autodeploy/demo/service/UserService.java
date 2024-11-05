package com.autodeploy.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autodeploy.demo.entity.User;
import com.autodeploy.demo.mapper.UserMapper;

import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public User register(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.findByEmail(email) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 实际应用中应该对密码进行加密
        user.setEmail(email);
        user.setUuid(UUID.randomUUID().toString());

        return userMapper.save(user);
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) { // 实际应用中应该对密码进行加密比较
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }

    public User getUserByUuid(String uuid) {
        return userMapper.findByUuid(uuid);
    }
} 