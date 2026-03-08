/**
 * VoxChain - Web3 Integration
 * Handles MetaMask connection and smart contract interaction via ethers.js
 */

// Contract ABI - This should match the deployed Voting.sol contract
const CONTRACT_ABI = [
    {
        "inputs": [],
        "stateMutability": "nonpayable",
        "type": "constructor"
    },
    {
        "anonymous": false,
        "inputs": [
            { "indexed": true, "internalType": "uint256", "name": "candidateId", "type": "uint256" },
            { "indexed": false, "internalType": "string", "name": "name", "type": "string" }
        ],
        "name": "CandidateAdded",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            { "indexed": false, "internalType": "uint256", "name": "timestamp", "type": "uint256" }
        ],
        "name": "ElectionEnded",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            { "indexed": false, "internalType": "uint256", "name": "timestamp", "type": "uint256" }
        ],
        "name": "ElectionStarted",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            { "indexed": true, "internalType": "address", "name": "voter", "type": "address" },
            { "indexed": true, "internalType": "uint256", "name": "candidateId", "type": "uint256" },
            { "indexed": false, "internalType": "uint256", "name": "timestamp", "type": "uint256" }
        ],
        "name": "VoteCast",
        "type": "event"
    },
    {
        "inputs": [{ "internalType": "string", "name": "_name", "type": "string" }],
        "name": "addCandidate",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "admin",
        "outputs": [{ "internalType": "address", "name": "", "type": "address" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [{ "internalType": "uint256", "name": "", "type": "uint256" }],
        "name": "candidates",
        "outputs": [
            { "internalType": "uint256", "name": "id", "type": "uint256" },
            { "internalType": "string", "name": "name", "type": "string" },
            { "internalType": "uint256", "name": "voteCount", "type": "uint256" }
        ],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "candidateCount",
        "outputs": [{ "internalType": "uint256", "name": "", "type": "uint256" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "electionActive",
        "outputs": [{ "internalType": "bool", "name": "", "type": "bool" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "endElection",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [{ "internalType": "uint256", "name": "_candidateId", "type": "uint256" }],
        "name": "getCandidate",
        "outputs": [
            { "internalType": "uint256", "name": "id", "type": "uint256" },
            { "internalType": "string", "name": "name", "type": "string" },
            { "internalType": "uint256", "name": "voteCount", "type": "uint256" }
        ],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "getCandidateCount",
        "outputs": [{ "internalType": "uint256", "name": "", "type": "uint256" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [{ "internalType": "address", "name": "", "type": "address" }],
        "name": "hasVoted",
        "outputs": [{ "internalType": "bool", "name": "", "type": "bool" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [{ "internalType": "address", "name": "_voter", "type": "address" }],
        "name": "hasVoterVoted",
        "outputs": [{ "internalType": "bool", "name": "", "type": "bool" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "isElectionActive",
        "outputs": [{ "internalType": "bool", "name": "", "type": "bool" }],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [],
        "name": "startElection",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [{ "internalType": "uint256", "name": "_candidateId", "type": "uint256" }],
        "name": "vote",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    }
];

// State
let provider = null;
let signer = null;
let contract = null;
let currentAccount = null;

// Get contract address from the page meta tag (set by backend)
function getContractAddress() {
    const meta = document.querySelector('meta[name="contract-address"]');
    return meta ? meta.getAttribute('content') : '';
}

/**
 * Check if MetaMask is installed
 */
function isMetaMaskInstalled() {
    return typeof window.ethereum !== 'undefined' && window.ethereum.isMetaMask;
}

/**
 * Connect to MetaMask wallet
 */
async function connectWallet() {
    if (!isMetaMaskInstalled()) {
        showToast('MetaMask is not installed. Please install MetaMask to continue.', 'error');
        window.open('https://metamask.io/download/', '_blank');
        return null;
    }

    try {
        // Request account access
        const accounts = await window.ethereum.request({
            method: 'eth_requestAccounts'
        });

        if (accounts.length > 0) {
            currentAccount = accounts[0];

            // Initialize ethers provider
            provider = new ethers.BrowserProvider(window.ethereum);
            signer = await provider.getSigner();

            // Initialize contract if address is configured
            const contractAddress = getContractAddress();
            if (contractAddress && contractAddress !== 'YOUR_CONTRACT_ADDRESS_HERE') {
                contract = new ethers.Contract(contractAddress, CONTRACT_ABI, signer);
            }

            // Store in session
            sessionStorage.setItem('walletAddress', currentAccount);

            // Update UI
            updateWalletUI(currentAccount);
            showToast('Wallet connected successfully!', 'success');

            return currentAccount;
        }
    } catch (error) {
        console.error('Error connecting wallet:', error);
        if (error.code === 4001) {
            showToast('Connection rejected by user', 'warning');
        } else {
            showToast('Error connecting wallet: ' + error.message, 'error');
        }
        return null;
    }
}

/**
 * Disconnect wallet
 */
function disconnectWallet() {
    currentAccount = null;
    provider = null;
    signer = null;
    contract = null;
    sessionStorage.removeItem('walletAddress');
    updateWalletUI(null);
    showToast('Wallet disconnected', 'warning');
}

/**
 * Get connected wallet address
 */
function getConnectedWallet() {
    return currentAccount || sessionStorage.getItem('walletAddress');
}

/**
 * Update wallet display across UI elements
 */
function updateWalletUI(address) {
    const walletBadges = document.querySelectorAll('.wallet-badge-text');
    const connectButtons = document.querySelectorAll('.connect-wallet-btn');
    const disconnectButtons = document.querySelectorAll('.disconnect-wallet-btn');
    const walletDisplays = document.querySelectorAll('.wallet-address-display');

    if (address) {
        const shortened = address.substring(0, 6) + '...' + address.substring(38);

        walletBadges.forEach(el => {
            el.textContent = shortened;
            el.closest('.wallet-badge').style.display = 'flex';
        });

        connectButtons.forEach(el => el.style.display = 'none');
        disconnectButtons.forEach(el => el.style.display = 'inline-flex');
        walletDisplays.forEach(el => {
            el.textContent = address;
            el.style.display = 'block';
        });
    } else {
        walletBadges.forEach(el => {
            el.closest('.wallet-badge').style.display = 'none';
        });
        connectButtons.forEach(el => el.style.display = 'inline-flex');
        disconnectButtons.forEach(el => el.style.display = 'none');
        walletDisplays.forEach(el => el.style.display = 'none');
    }
}

/**
 * Cast a vote on the blockchain
 */
async function castVoteOnBlockchain(candidateId) {
    if (!contract) {
        // If contract is not configured, just proceed with backend-only voting
        return null;
    }

    try {
        const tx = await contract.vote(candidateId);
        showToast('Transaction submitted. Waiting for confirmation...', 'warning');
        const receipt = await tx.wait();
        return receipt.hash;
    } catch (error) {
        console.error('Blockchain vote error:', error);
        if (error.reason) {
            throw new Error(error.reason);
        }
        throw error;
    }
}

/**
 * Check if voter has voted on blockchain
 */
async function checkBlockchainVoteStatus(address) {
    if (!contract) return false;
    try {
        return await contract.hasVoterVoted(address);
    } catch (error) {
        console.error('Error checking vote status:', error);
        return false;
    }
}

/**
 * Auto-reconnect on page load
 */
async function autoReconnect() {
    const savedAddress = sessionStorage.getItem('walletAddress');
    if (savedAddress && isMetaMaskInstalled()) {
        try {
            const accounts = await window.ethereum.request({ method: 'eth_accounts' });
            if (accounts.length > 0) {
                currentAccount = accounts[0];
                provider = new ethers.BrowserProvider(window.ethereum);
                signer = await provider.getSigner();

                const contractAddress = getContractAddress();
                if (contractAddress && contractAddress !== 'YOUR_CONTRACT_ADDRESS_HERE') {
                    contract = new ethers.Contract(contractAddress, CONTRACT_ABI, signer);
                }

                updateWalletUI(currentAccount);
            }
        } catch (error) {
            console.error('Auto-reconnect error:', error);
        }
    }
}

// Listen for account changes
if (window.ethereum) {
    window.ethereum.on('accountsChanged', function (accounts) {
        if (accounts.length === 0) {
            disconnectWallet();
        } else {
            currentAccount = accounts[0];
            sessionStorage.setItem('walletAddress', currentAccount);
            updateWalletUI(currentAccount);
            showToast('Account changed to ' + currentAccount.substring(0, 6) + '...', 'warning');
        }
    });

    window.ethereum.on('chainChanged', function () {
        window.location.reload();
    });
}

// Auto-reconnect on page load
document.addEventListener('DOMContentLoaded', autoReconnect);
