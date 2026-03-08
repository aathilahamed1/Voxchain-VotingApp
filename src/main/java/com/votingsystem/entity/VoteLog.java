package com.votingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes_log")
public class VoteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_wallet", nullable = false, length = 42)
    private String voterWallet;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "transaction_hash", length = 66)
    private String transactionHash;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    public VoteLog() {}

    public VoteLog(String voterWallet, Long candidateId, String transactionHash) {
        this.voterWallet = voterWallet;
        this.candidateId = candidateId;
        this.transactionHash = transactionHash;
        this.votedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVoterWallet() { return voterWallet; }
    public void setVoterWallet(String voterWallet) { this.voterWallet = voterWallet; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
