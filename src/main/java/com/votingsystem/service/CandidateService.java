package com.votingsystem.service;

import com.votingsystem.entity.Candidate;
import com.votingsystem.entity.Election;
import com.votingsystem.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionService electionService;

    public Candidate addCandidate(String name, String party, String description, Long electionId) {
        Election election = electionService.getElectionById(electionId);
        if ("ACTIVE".equals(election.getStatus())) {
            throw new RuntimeException("Cannot add candidates to an active election");
        }
        Candidate candidate = new Candidate(name, party, description, election);
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElectionId(electionId);
    }

    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    public void deleteCandidate(Long id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (candidate.isPresent() && candidate.get().getElection() != null
                && "ACTIVE".equals(candidate.get().getElection().getStatus())) {
            throw new RuntimeException("Cannot delete candidates from an active election");
        }
        candidateRepository.deleteById(id);
    }

    public long getCandidateCount() {
        return candidateRepository.count();
    }

    public long getCandidateCountByElection(Long electionId) {
        return candidateRepository.countByElectionId(electionId);
    }
}
