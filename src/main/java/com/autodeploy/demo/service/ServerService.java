package com.autodeploy.demo.service;

import com.autodeploy.demo.entity.Server;
import com.autodeploy.demo.repository.ServerRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Properties;

@Service
public class ServerService {
    
    @Autowired
    private ServerRepository serverRepository;
    

    
    public Server createServer(String useruuid, String ipAddress, String port, String username, String password, String gathererPath) {
        Server server = new Server(useruuid, ipAddress, port, username, password, gathererPath);
        return serverRepository.save(server);
    }
    
    public List<Server> findServersByUserUuid(String useruuid) {
        return serverRepository.findByUseruuid(useruuid);
    }
    
    public boolean verifyServerPassword(Server server, String rawPassword) {
        return server.checkPassword(rawPassword);
    }
    
    @Transactional
    public boolean testConnection(String serverId) {
        Server server = serverRepository.findById(serverId)
            .orElseThrow(() -> new RuntimeException("服务器不存在"));
            
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(
                server.getUsername(),
                server.getIpAddress(),
                Integer.parseInt(server.getPort())
            );
            
            // 设置密码
            session.setPassword(server.getPassword());
            
            // 设置连接属性
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // 不验证主机密钥
            session.setConfig(config);
            
            // 设置连接超时时间（毫秒）
            session.connect(5000);
            
            return true;
        } catch (JSchException e) {
            // 记录具体的错误信息
            String errorMessage = String.format(
                "连接服务器失败 - IP: %s, Port: %s, Username: %s, 错误: %s",
                server.getIpAddress(),
                server.getPort(),
                server.getUsername(),
                e.getMessage()
            );
            throw new RuntimeException(errorMessage, e);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    // 带有详细错误信息的测试连接方法
    @Transactional
    public String testConnectionWithDetails(String serverId) {
        Server server = serverRepository.findById(serverId)
            .orElseThrow(() -> new RuntimeException("服务器不存在"));
            
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(
                server.getUsername(),
                server.getIpAddress(),
                Integer.parseInt(server.getPort())
            );
            
            session.setPassword(server.getPassword());
            
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            session.connect(5000);
            
            return "连接成功！服务器可以正常访问。";
        } catch (JSchException e) {
            String errorDetail;
            if (e.getMessage().contains("Auth fail")) {
                errorDetail = "认证失败：用户名或密码错误";
            } else if (e.getMessage().contains("connect timed out")) {
                errorDetail = "连接超时：请检查IP地址和端口是否正确，以及网络是否通畅";
            } else if (e.getMessage().contains("Connection refused")) {
                errorDetail = "连接被拒绝：目标服务器拒绝连接，请检查SSH服务是否启动";
            } else {
                errorDetail = "连接错误：" + e.getMessage();
            }
            
            return "连接失败 - " + errorDetail;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 使用直接参数测试SSH连接
     */
    public String testDirectConnection(String ipAddress, String port, String username, String password) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(
                username,
                ipAddress,
                Integer.parseInt(port)
            );
            
            session.setPassword(password);
            
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 设置连接超时时间（毫秒）
            session.connect(5000);
            
            return "连接成功！服务器可以正常访问。";
        } catch (JSchException e) {
            String errorDetail;
            if (e.getMessage().contains("Auth fail")) {
                errorDetail = "认证失败：用户名或密码错误";
            } else if (e.getMessage().contains("connect timed out")) {
                errorDetail = "连接超时：请检查IP地址和端口是否正确，以及网络是否通畅";
            } else if (e.getMessage().contains("Connection refused")) {
                errorDetail = "连接被拒绝：目标服务器拒绝连接，请检查SSH服务是否启动";
            } else if (e.getMessage().contains("UnknownHostException")) {
                errorDetail = "未知主机：无法解析服务器地址，请检查IP地址是否正确";
            } else {
                errorDetail = "连接错误：" + e.getMessage();
            }
            
            return "连接失败 - " + errorDetail;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public Server findServerById(String serverId) {
        return serverRepository.findById(serverId)
            .orElseThrow(() -> new RuntimeException("服务器不存在"));
    }

    public void deleteServer(String serverId) {
        serverRepository.deleteById(serverId);
    }

} 