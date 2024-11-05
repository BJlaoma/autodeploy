
package com.autodeploy.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.autodeploy.demo.entity.User;
import com.autodeploy.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody JSONObject request) {
        try {
            User user = userService.register(
                request.getString("username"),
                request.getString("password"),
                request.getString("email")
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JSONObject request) {
        try {
            User user = userService.login(
                request.getString("username"),
                request.getString("password")
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
} 