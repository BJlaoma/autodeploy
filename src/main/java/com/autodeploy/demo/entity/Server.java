package com.autodeploy.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import com.autodeploy.demo.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Entity
@Table(name = "servers")
public class Server {
    
    @Transient // 不持久化到数据库
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String serverId;
    
    @Column(name = "user_uuid", nullable = false)
    private String useruuid;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column(nullable = false)
    private String port;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "gatherer_path")
    private String gathererPath;
    
    public Server() {}
    
    public Server(String useruuid, String ipAddress, String port, String username, String password, String gathererPath) {
        this.useruuid = useruuid;
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.password = password;
        if (gathererPath != "") {
            this.gathererPath = gathererPath;
        }else{
            this.gathererPath = "默认地址";
        }
    }

    public void setPassword(String password) {
        if (passwordEncoder != null) {
            this.password = passwordEncoder.encode(password);
        } else {
            this.password = password;
        }
    }

    // 验证密码
    public boolean checkPassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
} 