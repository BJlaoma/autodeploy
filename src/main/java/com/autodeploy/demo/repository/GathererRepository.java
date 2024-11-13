package com.autodeploy.demo.repository;

import com.autodeploy.demo.entity.Gatherer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GathererRepository extends JpaRepository<Gatherer, String> {
    
    // 根据deployerId查找所有相关的采集器
    List<Gatherer> findByDeployerId(String deployerId);
    
    // 根据projectId查找所有相关的采集器
    List<Gatherer> findByProjectId(String projectId);
    
    // 根据gathererName查找采集器
    Gatherer findByGathererName(String gathererName);
    
    // 根据deployerId和projectId组合查询
    List<Gatherer> findByDeployerIdAndProjectId(String deployerId, String projectId);
} 