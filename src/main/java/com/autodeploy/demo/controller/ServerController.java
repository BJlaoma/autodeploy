package com.autodeploy.demo.controller;

import com.autodeploy.demo.entity.Server;
import com.autodeploy.demo.service.ServerService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    @Autowired
    private ServerService serverService;

    // 创建新服务器
    @PostMapping
    public ResponseEntity<?> createServer(@RequestBody JSONObject serverData) {
        try {
            Server server = serverService.createServer(
                serverData.getString("useruuid"),
                serverData.getString("ipAddress"),
                serverData.getString("port"),
                serverData.getString("username"),
                serverData.getString("password"),
                serverData.getString("gathererPath")
            );
            return ResponseEntity.ok(server);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建服务器失败：" + e.getMessage());
        }
    }

    // 获取用户的所有服务器
    @GetMapping("/user/{useruuid}")
    public ResponseEntity<?> getServersByUser(@PathVariable String useruuid) {
        try {
            List<Server> servers = serverService.findServersByUserUuid(useruuid);
            return ResponseEntity.ok(servers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取服务器列表失败：" + e.getMessage());
        }
    }

    // 测试服务器连接（使用已保存的服务器ID）
    @PostMapping("/{serverId}/test")
    public ResponseEntity<?> testServerConnection(@PathVariable String serverId) {
        try {
            String result = serverService.testConnectionWithDetails(serverId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 测试直接连接（不保存服务器信息）
    @PostMapping("/test-connection")
    public ResponseEntity<?> testDirectConnection(@RequestBody JSONObject connectionData) {
        try {
            String result = serverService.testDirectConnection(
                connectionData.getString("ipAddress"),
                connectionData.getString("port"),
                connectionData.getString("username"),
                connectionData.getString("password")
            );
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{serverId}/delete")
    public ResponseEntity<?> deleteServer(@PathVariable String serverId) {
        serverService.deleteServer(serverId);
        return ResponseEntity.ok().build();
    }

} 