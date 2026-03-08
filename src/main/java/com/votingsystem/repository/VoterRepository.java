package com.votingsystem.repository;

import com.votingsystem.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByWalletAddress(String walletAddress);
    boolean existsByWalletAddress(String walletAddress);
}
