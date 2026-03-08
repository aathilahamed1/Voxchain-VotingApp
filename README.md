# 🗳️ DecentraVote — Decentralized Blockchain Voting System

A secure, transparent, blockchain-based voting system built with **Spring Boot**, **MySQL**, **Solidity Smart Contracts**, and **MetaMask** wallet authentication.

---

## 📋 Tech Stack

| Layer        | Technology                                  |
|-------------|---------------------------------------------|
| Frontend    | Thymeleaf, HTML5, CSS3, JavaScript, Ethers.js |
| Backend     | Java 17, Spring Boot 3.2, Spring Data JPA   |
| Database    | MySQL 8.0                                   |
| Blockchain  | Solidity 0.8.19, Hardhat, Ethereum          |
| Auth        | MetaMask Wallet                             |

---

## 🚀 Deployment Instructions

### Prerequisites

- **Java 17+**: [https://adoptium.net/](https://adoptium.net/)
- **Maven 3.8+**: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
- **Node.js 18+**: [https://nodejs.org/](https://nodejs.org/)
- **MySQL 8.0+**: [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/)
- **MetaMask**: [https://metamask.io/download/](https://metamask.io/download/)

---

### Step 1: Set Up MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create the database
CREATE DATABASE voting_system;
EXIT;
```

Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

### Step 2: Deploy the Smart Contract

```bash
# Navigate to blockchain directory
cd blockchain

# Install dependencies
npm install

# Start a local Hardhat node (keep this terminal running)
npx hardhat node

# In a NEW terminal, deploy the contract
cd blockchain
npx hardhat run scripts/deploy.js --network localhost
```

Copy the deployed **contract address** from the terminal output.

Update `src/main/resources/application.properties`:
```properties
blockchain.contract-address=0xYOUR_DEPLOYED_CONTRACT_ADDRESS
```

---

### Step 3: Configure MetaMask

1. Open MetaMask in your browser
2. Click **Networks** → **Add Network** → **Add a network manually**
3. Enter:
   - **Network Name**: Hardhat Local
   - **RPC URL**: `http://127.0.0.1:8545`
   - **Chain ID**: `31337`
   - **Currency Symbol**: ETH
4. Import a test account using one of the private keys printed by `npx hardhat node`

---

### Step 4: Run the Spring Boot Application

```bash
# From the project root directory
mvn clean install
mvn spring-boot:run
```

The application will start at: **http://localhost:8080**

---

### Step 5: Use the System

1. **Open** [http://localhost:8080](http://localhost:8080)
2. **Connect** your MetaMask wallet (ensure Hardhat Local network is selected)
3. **Admin Setup**: Go to `/admin`, login with `admin` / `admin123`
4. **Add Candidates** from the admin dashboard
5. **Start Election** from admin dashboard
6. **Register & Vote**: Users connect wallet → register → cast vote
7. **View Results**: Real-time results at `/results`

---

## 📁 Project Structure

```
VotingSystem1/
├── pom.xml                              # Maven configuration
├── README.md                            # This file
├── blockchain/                          # Smart contract
│   ├── package.json
│   ├── hardhat.config.js
│   ├── contracts/
│   │   └── Voting.sol                   # Solidity smart contract
│   └── scripts/
│       └── deploy.js                    # Deployment script
└── src/main/
    ├── java/com/votingsystem/
    │   ├── VotingSystemApplication.java # Main app
    │   ├── config/
    │   │   └── WebConfig.java           # CORS config
    │   ├── entity/                      # JPA entities
    │   │   ├── Voter.java
    │   │   ├── Candidate.java
    │   │   ├── Election.java
    │   │   └── VoteLog.java
    │   ├── repository/                  # Data repositories
    │   │   ├── VoterRepository.java
    │   │   ├── CandidateRepository.java
    │   │   ├── ElectionRepository.java
    │   │   └── VoteLogRepository.java
    │   ├── service/                     # Business logic
    │   │   ├── VoterService.java
    │   │   ├── CandidateService.java
    │   │   ├── ElectionService.java
    │   │   ├── VoteService.java
    │   │   └── BlockchainService.java
    │   └── controller/                  # REST & page controllers
    │       ├── PageController.java
    │       ├── VoterController.java
    │       ├── CandidateController.java
    │       ├── ElectionController.java
    │       └── VoteController.java
    └── resources/
        ├── application.properties       # App configuration
        ├── schema.sql                   # Database schema
        ├── static/
        │   ├── css/style.css            # Purple/White/Black theme
        │   └── js/
        │       ├── app.js               # UI logic
        │       └── web3-integration.js  # MetaMask & contract
        └── templates/                   # Thymeleaf templates
            ├── index.html               # Landing page
            ├── connect-wallet.html      # Wallet connect
            ├── register.html            # Voter registration
            ├── candidates.html          # Candidate list
            ├── vote.html                # Voting page
            ├── results.html             # Results page
            └── admin.html               # Admin dashboard
```

---

## 🔒 Security Features

- **Double vote prevention** — enforced in both smart contract and backend
- **Wallet-based auth** — MetaMask provides cryptographic authentication
- **Admin route protection** — login required for admin dashboard
- **Input validation** — all API endpoints validate incoming data
- **Immutable records** — votes recorded on Ethereum blockchain

---

## 📡 REST API Endpoints

| Method | Endpoint             | Description              |
|--------|----------------------|--------------------------|
| POST   | `/api/registerVoter` | Register a voter         |
| GET    | `/api/checkVoter`    | Check voter registration |
| POST   | `/api/addCandidate`  | Add a candidate          |
| GET    | `/api/candidates`    | List all candidates      |
| DELETE | `/api/candidates/{id}` | Delete a candidate     |
| POST   | `/api/vote`          | Cast a vote              |
| GET    | `/api/results`       | Get election results     |
| GET    | `/api/hasVoted`      | Check if voted           |
| POST   | `/api/startElection` | Start the election       |
| POST   | `/api/endElection`   | End the election         |
| GET    | `/api/election/status` | Get election status    |

---

## 🎨 Theme

The UI follows a strict color theme:
- **Primary**: Purple `#6A0DAD`
- **Secondary**: White `#FFFFFF`
- **Accent**: Black `#000000`

---

## ⚠️ Notes

- The smart contract is configured for **local Hardhat network only**
- For testnet/mainnet deployment, update RPC URLs and provide funded accounts
- Default admin credentials: `admin` / `admin123` — change in production
- Ensure MetaMask is connected to the correct network (Hardhat Local, Chain ID: 31337)
