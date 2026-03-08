package com.votingsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for blockchain interaction.
 * In production, this would use Web3j to interact with the deployed smart
 * contract.
 * For this implementation, the primary blockchain interaction happens
 * client-side
 * via MetaMask and ethers.js, while this service provides contract
 * configuration.
 */
@Service
public class BlockchainService {

    @Value("${web3j.client-address:http://127.0.0.1:8545}")
    private String rpcUrl;

    @Value("${blockchain.contract-address:}")
    private String contractAddress;

    public String getContractAddress() {
        return contractAddress;
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public boolean isContractConfigured() {
        return contractAddress != null
                && !contractAddress.isEmpty()
                && !contractAddress.equals("YOUR_CONTRACT_ADDRESS_HERE");
    }
}
