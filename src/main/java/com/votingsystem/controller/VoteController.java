package com.votingsystem.controller;

import com.votingsystem.entity.VoteLog;
import com.votingsystem.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<?> castVote(@RequestBody Map<String, Object> request) {
        try {
            String voterWallet = (String) request.get("voterWallet");
            Long candidateId = Long.valueOf(request.get("candidateId").toString());
            String transactionHash = (String) request.get("transactionHash");

            if (voterWallet == null || voterWallet.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Wallet address is required"));
            }

            VoteLog voteLog = voteService.castVote(voterWallet, candidateId, transactionHash);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vote cast successfully!",
                    "transactionHash", voteLog.getTransactionHash() != null ? voteLog.getTransactionHash() : "",
                    "candidateId", voteLog.getCandidateId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        List<Map<String, Object>> results = voteService.getResults();
        long totalVotes = voteService.getTotalVotes();
        return ResponseEntity.ok(Map.of(
                "results", results,
                "totalVotes", totalVotes));
    }

    @GetMapping("/hasVoted")
    public ResponseEntity<?> hasVoted(@RequestParam String walletAddress) {
        boolean voted = voteService.hasVoted(walletAddress);
        return ResponseEntity.ok(Map.of("hasVoted", voted));
    }
}
