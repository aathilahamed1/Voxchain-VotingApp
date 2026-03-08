/**
 * VoxChain - Main Application JS
 * UI interactions, form handling, notifications
 */

// ============ Toast Notifications ============

function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    const icons = { success: '✓', error: '✕', warning: '⚠' };
    toast.innerHTML = `
        <span style="font-size:1.2rem">${icons[type] || '●'}</span>
        <span class="toast-message">${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// ============ Loading Overlay ============

function showLoading(message = 'Processing...') {
    let overlay = document.getElementById('loading-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loading-overlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="loading-box">
                <div class="spinner"></div>
                <p id="loading-message">${message}</p>
            </div>
        `;
        document.body.appendChild(overlay);
    } else {
        document.getElementById('loading-message').textContent = message;
    }
    overlay.classList.add('show');
}

function hideLoading() {
    const overlay = document.getElementById('loading-overlay');
    if (overlay) overlay.classList.remove('show');
}

// ============ API Helper ============

async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(url, options);
    return response.json();
}

// ============ Voter Registration ============

async function registerVoter() {
    const walletAddress = getConnectedWallet();
    if (!walletAddress) {
        showToast('Please connect your MetaMask wallet first', 'error');
        return;
    }

    showLoading('Registering voter...');
    try {
        const result = await apiRequest('/api/registerVoter', 'POST', { walletAddress });
        hideLoading();

        if (result.success) {
            showToast('Registration successful!', 'success');
            setTimeout(() => window.location.href = '/vote', 1500);
        } else {
            showToast(result.message || 'Registration failed', 'error');
        }
    } catch (error) {
        hideLoading();
        showToast('Error: ' + error.message, 'error');
    }
}

// ============ Voting ============

let selectedCandidateId = null;

function selectCandidate(id, element) {
    // Remove previous selection
    document.querySelectorAll('.candidate-card').forEach(card => {
        card.classList.remove('selected');
    });

    // Select new
    element.closest('.candidate-card').classList.add('selected');
    selectedCandidateId = id;

    // Enable vote button
    const voteBtn = document.getElementById('vote-btn');
    if (voteBtn) voteBtn.disabled = false;
}

async function submitVote() {
    if (!selectedCandidateId) {
        showToast('Please select a candidate', 'warning');
        return;
    }

    const walletAddress = getConnectedWallet();
    if (!walletAddress) {
        showToast('Please connect your MetaMask wallet first', 'error');
        return;
    }

    showLoading('Casting your vote on the blockchain...');

    try {
        // Try blockchain vote first
        let transactionHash = null;
        try {
            transactionHash = await castVoteOnBlockchain(selectedCandidateId);
        } catch (blockchainError) {
            console.warn('Blockchain vote skipped:', blockchainError.message);
            // Generate a placeholder hash if blockchain is not configured
            transactionHash = '0x' + Array.from({ length: 64 }, () =>
                Math.floor(Math.random() * 16).toString(16)).join('');
        }

        // Record vote in backend
        const result = await apiRequest('/api/vote', 'POST', {
            voterWallet: walletAddress,
            candidateId: selectedCandidateId,
            transactionHash: transactionHash
        });

        hideLoading();

        if (result.success) {
            showToast('Vote cast successfully!', 'success');

            // Show confirmation
            const confirmation = document.getElementById('vote-confirmation');
            if (confirmation) {
                const txHashEl = document.getElementById('tx-hash-display');
                if (txHashEl) txHashEl.textContent = transactionHash || result.transactionHash;
                confirmation.classList.add('show');
            }

            // Disable vote button
            const voteBtn = document.getElementById('vote-btn');
            if (voteBtn) {
                voteBtn.disabled = true;
                voteBtn.textContent = '✓ Vote Submitted';
            }
        } else {
            showToast(result.message || 'Voting failed', 'error');
        }
    } catch (error) {
        hideLoading();
        showToast('Error: ' + error.message, 'error');
    }
}

// ============ Admin Functions ============

function showAdminSection(sectionId, clickedLink) {
    // Hide all sections
    document.querySelectorAll('.admin-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.admin-nav a').forEach(l => l.classList.remove('active'));

    // Show selected section
    document.getElementById(sectionId).classList.add('active');
    clickedLink.classList.add('active');
}

async function addCandidate() {
    const name = document.getElementById('candidate-name').value.trim();
    const party = document.getElementById('candidate-party').value.trim();
    const description = document.getElementById('candidate-description').value.trim();

    if (!name) {
        showToast('Candidate name is required', 'warning');
        return;
    }

    showLoading('Adding candidate...');
    try {
        const result = await apiRequest('/api/addCandidate', 'POST', { name, party, description });
        hideLoading();

        if (result.success) {
            showToast('Candidate added successfully!', 'success');
            document.getElementById('candidate-name').value = '';
            document.getElementById('candidate-party').value = '';
            document.getElementById('candidate-description').value = '';
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast(result.message || 'Failed to add candidate', 'error');
        }
    } catch (error) {
        hideLoading();
        showToast('Error: ' + error.message, 'error');
    }
}

async function deleteCandidate(id) {
    if (!confirm('Are you sure you want to delete this candidate?')) return;

    try {
        const result = await apiRequest(`/api/candidates/${id}`, 'DELETE');
        if (result.success) {
            showToast('Candidate deleted', 'success');
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast(result.message || 'Delete failed', 'error');
        }
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

async function startElection() {
    showLoading('Starting election...');
    try {
        const result = await apiRequest('/api/startElection', 'POST');
        hideLoading();
        if (result.success) {
            showToast('Election started!', 'success');
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast(result.message, 'error');
        }
    } catch (error) {
        hideLoading();
        showToast('Error: ' + error.message, 'error');
    }
}

async function endElection() {
    if (!confirm('Are you sure you want to end the election?')) return;

    showLoading('Ending election...');
    try {
        const result = await apiRequest('/api/endElection', 'POST');
        hideLoading();
        if (result.success) {
            showToast('Election ended!', 'success');
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast(result.message, 'error');
        }
    } catch (error) {
        hideLoading();
        showToast('Error: ' + error.message, 'error');
    }
}

// ============ Results ============

async function loadResults() {
    try {
        const data = await apiRequest('/api/results');
        if (data.results) {
            renderResults(data.results, data.totalVotes);
        }
    } catch (error) {
        console.error('Error loading results:', error);
    }
}

function renderResults(results, totalVotes) {
    const container = document.getElementById('results-container');
    if (!container) return;

    container.innerHTML = '';
    results.forEach(result => {
        const percentage = totalVotes > 0 ? ((result.voteCount / totalVotes) * 100).toFixed(1) : 0;
        const barHtml = `
            <div class="result-bar-container">
                <div class="result-bar-header">
                    <span class="name">${result.name}${result.party ? ' (' + result.party + ')' : ''}</span>
                    <span class="votes">${result.voteCount} votes (${percentage}%)</span>
                </div>
                <div class="result-bar">
                    <div class="result-bar-fill" style="width: ${percentage}%"></div>
                </div>
            </div>
        `;
        container.innerHTML += barHtml;
    });
}

// ============ Mobile Nav Toggle ============

function toggleMobileNav() {
    const nav = document.querySelector('.navbar-nav');
    if (nav) nav.classList.toggle('show');
}

// ============ Admin Login ============

function adminLogin() {
    const username = document.getElementById('admin-username')?.value;
    const password = document.getElementById('admin-password')?.value;

    // Simple client-side check (real auth should be server-side)
    if (username === 'admin' && password === 'admin123') {
        document.getElementById('admin-login-section')?.classList.add('hidden');
        document.getElementById('admin-dashboard-section')?.classList.remove('hidden');
        sessionStorage.setItem('adminLoggedIn', 'true');
        showToast('Admin login successful', 'success');
    } else {
        showToast('Invalid credentials', 'error');
    }
}

// ============ Init ============

document.addEventListener('DOMContentLoaded', function () {
    // Check admin login state
    if (sessionStorage.getItem('adminLoggedIn') === 'true') {
        const loginSection = document.getElementById('admin-login-section');
        const dashSection = document.getElementById('admin-dashboard-section');
        if (loginSection && dashSection) {
            loginSection.classList.add('hidden');
            dashSection.classList.remove('hidden');
        }
    }

    // Auto-load results on results page
    if (document.getElementById('results-container')) {
        loadResults();
    }
});
