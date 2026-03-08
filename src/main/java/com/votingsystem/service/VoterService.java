package com.votingsystem.service;

import com.votingsystem.entity.Voter;
import com.votingsystem.repository.VoterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoterService {

    @Autowired
    private VoterRepository voterRepository;

    public Voter registerVoter(String walletAddress) {
        if (voterRepository.existsByWalletAddress(walletAddress.toLowerCase())) {
            throw new RuntimeException("Voter already registered with this wallet address");
        }
        Voter voter = new Voter(walletAddress.toLowerCase());
        return voterRepository.save(voter);
    }

    public boolean isRegistered(String walletAddress) {
        return voterRepository.existsByWalletAddress(walletAddress.toLowerCase());
    }

    public Optional<Voter> findByWallet(String walletAddress) {
        return voterRepository.findByWalletAddress(walletAddress.toLowerCase());
    }

    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }

    public long getVoterCount() {
        return voterRepository.count();
    }
}
