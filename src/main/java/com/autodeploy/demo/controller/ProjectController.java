package com.autodeploy.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.autodeploy.demo.entity.Project;
import com.autodeploy.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody JSONObject request) {
        try {
            Project project = projectService.createProject(
                request.getString("userUuid"),
                request.getString("projectName"),
                request.getString("message")
            );
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create-with-details")
    public ResponseEntity<?> createProjectWithDetails(@RequestBody JSONObject request) {
        try {
            Project project = projectService.createProjectWithDetails(
                request.getString("userUuid"),
                request.getString("projectName"),
                request.getString("appName"),
                request.getString("vision"),
                request.getString("message")
            );
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<?> getUserProjects(@PathVariable String userUuid) {
        try {
            List<Project> projects = projectService.getUserProjects(userUuid);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{projectId}/deployer")
    public ResponseEntity<?> addDeployer(@PathVariable String projectId, @RequestBody JSONObject request) {
        try {
            Project project = projectService.addDeployer(projectId, request.getString("deployerId"));
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{projectId}/gatherer")
    public ResponseEntity<?> addGatherer(@PathVariable String projectId, @RequestBody JSONObject request) {
        try {
            Project project = projectService.addGatherer(projectId, request.getString("gathererId"));
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 