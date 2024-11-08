package com.autodeploy.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String projectId;
    
    @Column(nullable = false)
    private String projectName;
    
    @Column(name = "user_uuid", nullable = false)
    private String useruuid;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "vision")
    private String vision;

    @Column(name = "message", nullable = false)
    private String message;
    
    // deployerIds 是部署任务的id
    @ElementCollection
    @CollectionTable(name = "project_deployers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "deployer_id")
    private List<String> deployerIds = new ArrayList<>();
    
    // gathererIds 是采集任务的id
    @ElementCollection
    @CollectionTable(name = "project_gatherers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "gatherer_id")
    private List<String> gathererIds = new ArrayList<>();

    public Project(String userUuid, String projectName, String message) {
        this.useruuid = userUuid;
        this.projectName = projectName;
        this.message = message;
        this.projectId = UUID.randomUUID().toString();
    }

    public Project(String userUuid, String projectName, String appName, String vision, String message) {
        this.useruuid = userUuid;
        this.projectName = projectName;
        this.appName = appName;
        this.vision = vision;
        this.message = message;
        this.projectId = UUID.randomUUID().toString();
    }
    
    public Project() {}
} 