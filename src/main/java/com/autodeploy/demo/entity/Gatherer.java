package com.autodeploy.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gatherers")
public class Gatherer {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String gathererId;
    
    @Column(name = "deployer_id", nullable = false)
    private String deployerId;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(name = "gatherer_name", nullable = false)
    private String gathererName;
    
    @Column(name = "agent_path", nullable = false)
    private String agentPath;
    
    @Column(name = "log_path")
    private String logPath;
    
    @Column(name = "gather_time")
    private LocalDateTime gatherTime;
    
    public Gatherer() {}
    
    public Gatherer(String deployerId, String projectId, String gathererName, String agentPath, String logPath) {
        this.deployerId = deployerId;
        this.projectId = projectId;
        this.gathererName = gathererName;
        if(agentPath.equals("default"))
            this.agentPath = "default";
        else{   
            this.agentPath = agentPath;
        }
        this.logPath = logPath != null ? logPath : "default";
        this.gatherTime = LocalDateTime.now();
    }
}
