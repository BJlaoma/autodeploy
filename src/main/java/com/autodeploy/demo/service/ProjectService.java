package com.autodeploy.demo.service;

import com.autodeploy.demo.entity.Project;
import com.autodeploy.demo.entity.User;
import com.autodeploy.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserService userService;

    public Project createProject(String userUuid, String projectName, String message) {
        User user = userService.getUserByUuid(userUuid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Project project = new Project(userUuid, projectName, message);
        return projectRepository.save(project);
    }

    public Project createProjectWithDetails(String userUuid, String projectName, 
                                         String appName, String vision, String message) {
        User user = userService.getUserByUuid(userUuid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Project project = new Project(userUuid, projectName, appName, vision, message);
        return projectRepository.save(project);
    }

    public Project addDeployer(String projectId, String deployerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.getDeployerIds().add(deployerId);
        return projectRepository.save(project);
    }

    public Project addGatherer(String projectId, String gathererId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.getGathererIds().add(gathererId);
        return projectRepository.save(project);
    }

    public List<Project> getUserProjects(String userUuid) {
        return projectRepository.findByUseruuid(userUuid);
    }
} 