package com.votingsystem.controller;

import com.votingsystem.entity.Election;
import com.votingsystem.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @PostMapping("/elections")
    public ResponseEntity<?> createElection(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Election election = electionService.createElection(name);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Election created successfully",
                    "election", electionToMap(election)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        List<Election> elections = electionService.getAllElections();
        List<Map<String, Object>> electionList = elections.stream()
                .map(this::electionToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(electionList);
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<?> getElection(@PathVariable("id") Long id) {
        try {
            Election election = electionService.getElectionById(id);
            return ResponseEntity.ok(electionToMap(election));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable("id") Long id) {
        try {
            electionService.deleteElection(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Election deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/elections/{id}/start")
    public ResponseEntity<?> startElection(@PathVariable("id") Long id) {
        try {
            Election election = electionService.startElection(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Election started successfully",
                    "status", election.getStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/elections/{id}/end")
    public ResponseEntity<?> endElection(@PathVariable("id") Long id) {
        try {
            Election election = electionService.endElection(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Election ended successfully",
                    "status", election.getStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/election/status")
    public ResponseEntity<?> getElectionStatus() {
        Optional<Election> active = electionService.getActiveElection();
        if (active.isPresent()) {
            Election election = active.get();
            return ResponseEntity.ok(Map.of(
                    "status", election.getStatus(),
                    "name", election.getName(),
                    "startTime", election.getStartTime() != null ? election.getStartTime().toString() : "",
                    "endTime", election.getEndTime() != null ? election.getEndTime().toString() : ""));
        }
        return ResponseEntity.ok(Map.of("status", "NOT_STARTED", "name", "", "startTime", "", "endTime", ""));
    }

    private Map<String, Object> electionToMap(Election election) {
        return Map.of(
                "id", election.getId(),
                "name", election.getName(),
                "status", election.getStatus(),
                "startTime", election.getStartTime() != null ? election.getStartTime().toString() : "",
                "endTime", election.getEndTime() != null ? election.getEndTime().toString() : "",
                "candidateCount", election.getCandidates() != null ? election.getCandidates().size() : 0);
    }
}
