package com.autodeploy.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autodeploy.demo.service.GatherService;
import com.autodeploy.demo.entity.Gatherer;
@RestController
@RequestMapping("/api/gatherers")
public class GatherController {

    @Autowired
    private GatherService gatherService;

    @PostMapping("/create")
    public ResponseEntity<Gatherer> createGatherer(@RequestBody Gatherer gatherer) {
        Gatherer createdGatherer = gatherService.createGatherer(gatherer.getDeployerId(), gatherer.getProjectId(), gatherer.getGathererName(), gatherer.getAgentPath(), gatherer.getLogPath());
        return ResponseEntity.ok(createdGatherer);
    }

    @GetMapping("/{gathererId}")
    public ResponseEntity<Gatherer> getGathererById(@PathVariable String gathererId) {
        Gatherer gatherer = gatherService.getGathererById(gathererId);
        return ResponseEntity.ok(gatherer);
    }

    @DeleteMapping("/{gathererId}")
    public ResponseEntity<Void> deleteGatherer(@PathVariable String gathererId) {
        gatherService.deleteGatherer(gathererId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deployer/{deployerId}")
    public ResponseEntity<List<Gatherer>> getGatherersByDeployerId(@PathVariable String deployerId) {
        List<Gatherer> gatherers = gatherService.getGatherersByDeployerId(deployerId);
        return ResponseEntity.ok(gatherers);
    }
    
}
