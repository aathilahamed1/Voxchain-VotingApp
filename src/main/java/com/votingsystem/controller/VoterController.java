package com.votingsystem.controller;

import com.votingsystem.entity.Voter;
import com.votingsystem.service.VoterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class VoterController {

    @Autowired
    private VoterService voterService;

    @PostMapping("/registerVoter")
    public ResponseEntity<?> registerVoter(@RequestBody Map<String, String> request) {
        try {
            String walletAddress = request.get("walletAddress");
            if (walletAddress == null || walletAddress.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Wallet address is required"));
            }
            Voter voter = voterService.registerVoter(walletAddress);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Voter registered successfully",
                    "voter", Map.of(
                            "id", voter.getId(),
                            "walletAddress", voter.getWalletAddress(),
                            "registeredAt", voter.getRegisteredAt().toString())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/checkVoter")
    public ResponseEntity<?> checkVoter(@RequestParam String walletAddress) {
        boolean registered = voterService.isRegistered(walletAddress);
        return ResponseEntity.ok(Map.of("registered", registered));
    }
}
