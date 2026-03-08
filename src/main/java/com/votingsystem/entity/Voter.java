package com.votingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voters")
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_address", unique = true, nullable = false, length = 42)
    private String walletAddress;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Voter() {}

    public Voter(String walletAddress) {
        this.walletAddress = walletAddress;
        this.registeredAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWalletAddress() { return walletAddress; }
    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
