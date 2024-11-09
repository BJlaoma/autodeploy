package com.autodeploy.demo.controller;

import com.autodeploy.demo.service.FileService;
import com.alibaba.fastjson.JSONObject;
import com.autodeploy.demo.entity.Deployer;
import com.autodeploy.demo.service.DeployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deployers")
public class DeployerController {

    @Autowired
    private DeployerService deployerService;

    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseEntity<Deployer> createDeployer(
            @RequestParam("projectId") String projectId,
            @RequestParam("appName") String appName,
            @RequestParam("deployDir") String deployDir,
            @RequestParam(value = "shPath", required = false) String shPath,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "file", required = false) MultipartFile appFile,
            @RequestParam(value = "shFile", required = false) MultipartFile shFile) {
        try {
            Deployer deployer = deployerService.createDeployer(projectId, appName, deployDir, shPath, serverId);
            if (appFile != null) {
                fileService.uploadFileToServer(serverId, appFile, deployDir);
            }
            if (shFile != null) {
                fileService.uploadFileToServer(serverId, shFile, deployDir);
            }
            return ResponseEntity.ok(deployer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{deployerId}/servers")
    public ResponseEntity<Deployer> addServer(
            @PathVariable String deployerId,
            @RequestBody JSONObject request) {
        try {
            List<String> serverIds = request.getJSONArray("serverIds").toJavaList(String.class);
            if (serverIds == null) {
                return ResponseEntity.badRequest().build();
            }

            Deployer deployer = deployerService.addServers(deployerId, serverIds);
            return ResponseEntity.ok(deployer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Deployer>> getProjectDeployers(@PathVariable String projectId) {
        List<Deployer> deployers = deployerService.getProjectDeployers(projectId);
        return ResponseEntity.ok(deployers);
    }

    @GetMapping("/app/{appName}")
    public ResponseEntity<List<Deployer>> getDeployersByAppName(@PathVariable String appName) {
        List<Deployer> deployers = deployerService.getDeployersByAppName(appName);
        return ResponseEntity.ok(deployers);
    }

    @DeleteMapping("/{deployerId}")
    public ResponseEntity<Void> deleteDeployer(@PathVariable String deployerId) {
        try {
            deployerService.deleteDeployer(deployerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 