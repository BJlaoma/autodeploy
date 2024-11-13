package com.autodeploy.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.autodeploy.demo.entity.Gatherer;
import com.autodeploy.demo.repository.GathererRepository;

@Service
public class GatherService {
    @Autowired
    private GathererRepository gathererRepository;

    public void addGatherer(Gatherer gatherer){
        gathererRepository.save(gatherer);
    }

    public Gatherer getGathererById(String gathererId){
        return gathererRepository.findById(gathererId).orElse(null);
    }

    public void deleteGatherer(String gathererId){
        gathererRepository.deleteById(gathererId);
    }

    public List<Gatherer> getGatherersByDeployerId(String deployerId){
        return gathererRepository.findByDeployerId(deployerId);
    }

    public Gatherer createGatherer(String deployerId, String projectId, String gathererName, String agentPath, String logPath){
        return new Gatherer(deployerId, projectId, gathererName, agentPath, logPath);
    }
}
