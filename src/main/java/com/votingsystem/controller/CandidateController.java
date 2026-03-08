package com.votingsystem.controller;

import com.votingsystem.entity.Candidate;
import com.votingsystem.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/addCandidate")
    public ResponseEntity<?> addCandidate(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String party = request.get("party");
            String description = request.get("description");
            String electionIdStr = request.get("electionId");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Candidate name is required"));
            }

            if (electionIdStr == null || electionIdStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message",
                                "Election ID is required. Please select an election first."));
            }

            Long electionId = Long.parseLong(electionIdStr);
            Candidate candidate = candidateService.addCandidate(name, party, description, electionId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Candidate added successfully",
                    "candidate", Map.of(
                            "id", candidate.getId(),
                            "name", candidate.getName(),
                            "party", candidate.getParty() != null ? candidate.getParty() : "",
                            "description", candidate.getDescription() != null ? candidate.getDescription() : "")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/elections/{electionId}/candidates")
    public ResponseEntity<List<Candidate>> getCandidatesByElection(@PathVariable("electionId") Long electionId) {
        return ResponseEntity.ok(candidateService.getCandidatesByElection(electionId));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable("id") Long id) {
        try {
            candidateService.deleteCandidate(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Candidate deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
