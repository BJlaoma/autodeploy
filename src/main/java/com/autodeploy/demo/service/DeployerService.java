package com.autodeploy.demo.service;

import com.autodeploy.demo.entity.Deployer;
import com.autodeploy.demo.repository.DeployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeployerService {

    @Autowired
    private DeployerRepository deployerRepository;
    
    @Autowired
    private ServerService serverService;

    public Deployer createDeployer(String projectId, String appName, String deployDir, String shPath, String serverId) {
        if (serverId != null && serverService.findServerById(serverId) == null) {
            throw new RuntimeException("Server not found");
        }
        Deployer deployer = new Deployer(projectId, appName, deployDir, shPath, serverId);
        return deployerRepository.save(deployer);
    }

    public Deployer addServer(String deployerId, String serverId) {
        Deployer deployer = deployerRepository.findById(deployerId)
                .orElseThrow(() -> new RuntimeException("Deployer not found"));
        if (serverService.findServerById(serverId) == null) {
            throw new RuntimeException("Server not found");
        }
        deployer.getServerIds().add(serverId);
        return deployerRepository.save(deployer);
    }

    public Deployer addServers(String deployerId, List<String> serverIds) {
        Deployer deployer = deployerRepository.findById(deployerId)
                .orElseThrow(() -> new RuntimeException("Deployer not found"));
        deployer.getServerIds().addAll(serverIds);
        return deployerRepository.save(deployer);
    }

    public List<Deployer> getProjectDeployers(String projectId) {
        return deployerRepository.findByProjectId(projectId);
    }

    public List<Deployer> getDeployersByAppName(String appName) {
        return deployerRepository.findByAppName(appName);
    }

    public void deleteDeployer(String deployerId) {
        deployerRepository.deleteById(deployerId);
    }
} 