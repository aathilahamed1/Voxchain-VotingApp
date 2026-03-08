-- Voting System Database Schema

CREATE DATABASE IF NOT EXISTS voting_system;
USE voting_system;

-- Voters table
CREATE TABLE IF NOT EXISTS voters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_address VARCHAR(42) UNIQUE NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Candidates table
CREATE TABLE IF NOT EXISTS candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    party VARCHAR(100),
    description TEXT
);

-- Elections table
CREATE TABLE IF NOT EXISTS elections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) DEFAULT 'NOT_STARTED',
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL
);

-- Votes log table
CREATE TABLE IF NOT EXISTS votes_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voter_wallet VARCHAR(42) NOT NULL,
    candidate_id BIGINT NOT NULL,
    transaction_hash VARCHAR(66),
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

-- Insert initial election record
INSERT INTO elections (status) VALUES ('NOT_STARTED');
