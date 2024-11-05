
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
        
    @ElementCollection
    @CollectionTable(name = "project_deployers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "deployer_id")
    private List<String> deployerIds = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "project_gatherers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "gatherer_id")
    private List<String> gathererIds = new ArrayList<>();

    public Project(String userUuid, String projectName) {
        this.useruuid = userUuid;
        this.projectName = projectName;
        this.projectId = UUID.randomUUID().toString();
    }
    public Project() {}
} 