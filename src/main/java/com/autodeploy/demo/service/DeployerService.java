package com.autodeploy.demo.service;

import com.autodeploy.demo.entity.Deployer;
import com.autodeploy.demo.repository.DeployerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeployerService {

    private static final Logger logger = LoggerFactory.getLogger(DeployerService.class);
    
    @Autowired
    private DeployerRepository deployerRepository;
    
    @Autowired
    private ServerService serverService;
    
    @Autowired
    private FileService fileService;

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
        logger.info("获取项目 {} 的部署配置", projectId);
        try {
            return deployerRepository.findByProjectId(projectId);
        } catch (Exception e) {
            logger.error("获取项目部署配置失败", e);
            throw new RuntimeException("获取项目部署配置失败: " + e.getMessage());
        }
    }

    public List<Deployer> getDeployersByAppName(String appName) {
        return deployerRepository.findByAppName(appName);
    }

    public void deleteDeployer(String deployerId) {
        Deployer deployer = deployerRepository.findById(deployerId)
                .orElseThrow(() -> new RuntimeException("Deployer not found"));
        
        // 删除每个服务器上的部署目录和脚本
        for (String serverId : deployer.getServerIds()) {
            try {
                // 删除部署目录
                if (deployer.getDeployDir() != null && !deployer.getDeployDir().isEmpty()) {
                    logger.info("删除服务器{}上的部署目录: {}", serverId, deployer.getDeployDir());
                    fileService.deleteRemoteDirectory(serverId, deployer.getDeployDir());
                }
                
                // 删除脚本文件
                if (deployer.getShPath() != null && !deployer.getShPath().isEmpty()) {
                    logger.info("删除服务器{}上的脚本文件: {}", serverId, deployer.getShPath());
                    fileService.deleteRemoteDirectory(serverId, deployer.getShPath());
                }
            } catch (Exception e) {
                logger.error("删除服务器{}上的文件失败", serverId, e);
                // 可以选择继续删除其他服务器上的文件，或者抛出异常中断整个操作
                throw new RuntimeException("删除服务器文件失败: " + e.getMessage());
            }
        }
        
        // 删除数据库中的记录
        deployerRepository.deleteById(deployerId);
        logger.info("部署配置{}已删除", deployerId);
    }

    public boolean deploy(String deployerId) {
        Deployer deployer = deployerRepository.findById(deployerId)
                .orElseThrow(() -> new RuntimeException("Deployer not found"));
        
        return true;
    }   

    public Deployer getDeployerById(String deployerId) {
        return deployerRepository.findById(deployerId)
                .orElseThrow(() -> new RuntimeException("Deployer not found"));
    }

    public Deployer updateDeployer(Deployer deployer) {
        return deployerRepository.save(deployer);
    }

    public Deployer removeServer(String deployerId, String serverId) {
        Deployer deployer = getDeployerById(deployerId);
        deployer.getServerIds().remove(serverId);
        return deployerRepository.save(deployer);
    }
} 