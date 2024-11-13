package com.autodeploy.demo.service;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.autodeploy.demo.entity.Server;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

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
        ChannelExec channel = null;
        
        try {
            // 创建SSH会话
            session = jsch.getSession(server.getUsername(), server.getIpAddress(), Integer.parseInt(server.getPort()));
            session.setPassword(server.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 首先检查并创建目录
            String directory = targetPath.substring(0, targetPath.lastIndexOf('/'));
            String checkAndCreateDirCommand = "mkdir -p " + directory;
            
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(checkAndCreateDirCommand);
            channel.connect();
            
            // 等待目录创建完成
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }
            
            // 检查命令执行结果
            if (channel.getExitStatus() != 0) {
                throw new RuntimeException("Failed to create directory: " + directory);
            }
            
            // 关闭之前的channel
            channel.disconnect();
            
            // 使用SCP传输文件
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -t " + targetPath);
            
            try (InputStream in = channel.getInputStream();
                 OutputStream out = channel.getOutputStream()) {
                
                channel.connect();
                
                // 检查服务器响应
                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP initialization failed");
                }
                
                // 发送文件信息
                String command = "C0644 " + file.getSize() + " " + file.getOriginalFilename() + "\n";
                out.write(command.getBytes());
                out.flush();
                
                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP file info failed");
                }
                
                // 发送文件内容
                try (InputStream fis = file.getInputStream()) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf, 0, buf.length)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                
                // 发送结束标记
                out.write(0);
                out.flush();
                
                if (checkAck(in) != 0) {
                    throw new RuntimeException("SCP transfer failed");
                }
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

    // 添加用于检查SCP响应的辅助方法
    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0) return 0;
        if (b == -1) return -1;
        
        if (b == 1 || b == 2) {
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            throw new RuntimeException("SCP error: " + sb.toString());
        }
        return b;
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

    public void deleteRemoteDirectory(String serverId, String targetPath) throws Exception {
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

            // 使用rm -rf命令删除目录及其内容
            String command = "rm -rf " + targetPath;
            
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            
            // 获取错误输出流
            InputStream errStream = channel.getErrStream();
            
            channel.connect();
            
            // 读取错误输出
            byte[] tmp = new byte[1024];
            StringBuilder errorMessage = new StringBuilder();
            while (true) {
                while (errStream.available() > 0) {
                    int i = errStream.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorMessage.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (errStream.available() > 0) continue;
                    if (channel.getExitStatus() != 0) {
                        throw new RuntimeException("Failed to delete directory: " + errorMessage.toString());
                    }
                    break;
                }
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