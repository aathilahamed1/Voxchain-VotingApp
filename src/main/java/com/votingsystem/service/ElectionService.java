package com.votingsystem.service;

import com.votingsystem.entity.Election;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private VoteLogRepository voteLogRepository;

    public Election createElection(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Election name is required");
        }
        Election election = new Election(name.trim());
        return electionRepository.save(election);
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found with ID: " + id));
    }

    @Transactional
    public void deleteElection(Long id) {
        getElectionById(id); // Ensure it exists
        voteLogRepository.deleteByElectionId(id); // Delete associated votes first
        electionRepository.deleteById(id);
    }

    public Optional<Election> getActiveElection() {
        return electionRepository.findByStatus("ACTIVE");
    }

    public Election startElection(Long electionId) {
        Election election = getElectionById(electionId);
        // Check if another election is already active
        Optional<Election> activeElection = getActiveElection();
        if (activeElection.isPresent() && !activeElection.get().getId().equals(electionId)) {
            throw new RuntimeException(
                    "Another election ('" + activeElection.get().getName() + "') is already active. End it first.");
        }
        if ("ACTIVE".equals(election.getStatus())) {
            throw new RuntimeException("This election is already active");
        }
        election.setStatus("ACTIVE");
        election.setStartTime(LocalDateTime.now());
        election.setEndTime(null);
        return electionRepository.save(election);
    }

    public Election endElection(Long electionId) {
        Election election = getElectionById(electionId);
        if (!"ACTIVE".equals(election.getStatus())) {
            throw new RuntimeException("This election is not currently active");
        }
        election.setStatus("ENDED");
        election.setEndTime(LocalDateTime.now());
        return electionRepository.save(election);
    }

    public String getStatus() {
        Optional<Election> active = getActiveElection();
        return active.isPresent() ? "ACTIVE" : "NOT_STARTED";
    }

    public boolean isActive() {
        return getActiveElection().isPresent();
    }
}
