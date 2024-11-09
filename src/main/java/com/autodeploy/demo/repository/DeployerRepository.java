package com.autodeploy.demo.repository;

import com.autodeploy.demo.entity.Deployer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployerRepository extends JpaRepository<Deployer, String> {
    List<Deployer> findByProjectId(String projectId);
    List<Deployer> findByAppName(String appName);
} 