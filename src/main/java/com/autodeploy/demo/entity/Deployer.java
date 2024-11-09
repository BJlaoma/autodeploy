package com.autodeploy.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "deployers")
public class Deployer {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String deployerId;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(name = "app_name", nullable = false)
    private String appName;
    
    @Column(name = "deploy_dir", nullable = false)
    private String deployDir;
    
    @Column(name = "sh_path")
    private String shPath;
    
    @Column(name = "deploy_time")
    private LocalDateTime deployTime;
    
    @Column(name = "server_id")
    private String serverId;
    
    @ElementCollection
    @CollectionTable(
        name = "deployer_servers",
        joinColumns = @JoinColumn(name = "deployer_id")
    )
    @Column(name = "server_id")
    private List<String> serverIds = new ArrayList<>();
    
    public Deployer() {}
    
    public Deployer(String projectId, String appName, String deployDir, String shPath, String serverId) {
        this.projectId = projectId;
        this.appName = appName;
        this.deployDir = deployDir;
        this.shPath = shPath != null ? shPath : "/default/deploy.sh";
        this.serverId = serverId;
        this.deployTime = LocalDateTime.now();
        if (serverId != null) {
            this.serverIds.add(serverId);
        }
    }
} 