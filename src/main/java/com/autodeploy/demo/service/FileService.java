package com.autodeploy.demo.service;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.autodeploy.demo.entity.Server;
import java.io.InputStream;

@Service
public class FileService {
    
    @Autowired
    private ServerService serverService;
    
    public void uploadFileToServer(String serverId, MultipartFile file, String targetPath) throws Exception {
        Server server = serverService.findServerById(serverId);
        if (server == null) {
            throw new RuntimeException("Server not found");
        }
        
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 创建SSH会话
            session = jsch.getSession(server.getUsername(), server.getIpAddress(), Integer.parseInt(server.getPort()));
            session.setPassword(server.getPassword());
            // 跳过主机密钥验证
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            // 打开SFTP通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                channelSftp.put(inputStream, targetPath);
            }
            
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void executeCommand(String serverId, String command) throws Exception {
        Server server = serverService.findServerById(serverId);
        if (server == null) {
            throw new RuntimeException("Server not found");
        }

        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            // 创建SSH会话
            session = jsch.getSession(server.getUsername(), server.getIpAddress(), Integer.parseInt(server.getPort()));
            session.setPassword(server.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 执行命令
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            // 等待命令执行完成
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
} 