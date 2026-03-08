package com.votingsystem.service;

import com.votingsystem.entity.Candidate;
import com.votingsystem.entity.VoteLog;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.VoteLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoteService {

    @Autowired
    private VoteLogRepository voteLogRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionService electionService;

    public VoteLog castVote(String voterWallet, Long candidateId, String transactionHash) {
        // Check if election is active
        if (!electionService.isActive()) {
            throw new RuntimeException("Election is not currently active");
        }

        // Check if voter already voted
        if (voteLogRepository.existsByVoterWallet(voterWallet.toLowerCase())) {
            throw new RuntimeException("You have already voted in this election");
        }

        // Verify candidate exists
        if (!candidateRepository.existsById(candidateId)) {
            throw new RuntimeException("Invalid candidate ID");
        }

        VoteLog voteLog = new VoteLog(voterWallet.toLowerCase(), candidateId, transactionHash);
        return voteLogRepository.save(voteLog);
    }

    public boolean hasVoted(String voterWallet) {
        return voteLogRepository.existsByVoterWallet(voterWallet.toLowerCase());
    }

    public List<Map<String, Object>> getResults() {
        Optional<com.votingsystem.entity.Election> activeElection = electionService.getActiveElection();
        if (activeElection.isEmpty()) {
            return new ArrayList<>();
        }
        Long electionId = activeElection.get().getId();

        List<Object[]> voteCounts = voteLogRepository.countVotesByElectionId(electionId);
        List<Candidate> allCandidates = candidateRepository.findByElectionId(electionId);
        List<Map<String, Object>> results = new ArrayList<>();

        Map<Long, Long> voteMap = new HashMap<>();
        for (Object[] row : voteCounts) {
            Long candidateId = ((Number) row[0]).longValue();
            Long count = ((Number) row[1]).longValue();
            voteMap.put(candidateId, count);
        }

        for (Candidate candidate : allCandidates) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", candidate.getId());
            result.put("name", candidate.getName());
            result.put("party", candidate.getParty());
            result.put("voteCount", voteMap.getOrDefault(candidate.getId(), 0L));
            results.add(result);
        }

        // Sort by vote count descending
        results.sort((a, b) -> Long.compare((Long) b.get("voteCount"), (Long) a.get("voteCount")));
        return results;
    }

    public long getTotalVotes() {
        Optional<com.votingsystem.entity.Election> activeElection = electionService.getActiveElection();
        if (activeElection.isEmpty()) {
            return 0;
        }
        return voteLogRepository.countByElectionId(activeElection.get().getId());
    }
}
