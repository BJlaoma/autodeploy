package com.autodeploy.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autodeploy.demo.entity.User;
import com.autodeploy.demo.mapper.UserMapper;

import java.util.UUID;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
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
        user.setPassword(password);
        user.setEmail(email);
        user.setUuid(UUID.randomUUID().toString());

        return userMapper.save(user);
    }

    public User login(String username, String password) {
        logger.info("尝试验证用户: {}", username);
        User user = userMapper.findByUsername(username);
        
        if (user == null) {
            logger.error("用户{}不存在", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!password.equals(user.getPassword())) {
            logger.error("用户{}密码错误", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        logger.info("用户{}验证成功", username);
        return user;
    }

    public User getUserByUuid(String uuid) {
        return userMapper.findByUuid(uuid);
    }
} 