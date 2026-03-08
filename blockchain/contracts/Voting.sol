// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract Voting {
    // ============ Structs ============
    struct CandidateInfo {
        uint256 id;
        string name;
        uint256 voteCount;
    }

    // ============ State Variables ============
    address public admin;
    bool public electionActive;
    uint256 public candidateCount;

    mapping(uint256 => CandidateInfo) public candidates;
    mapping(address => bool) public hasVoted;

    // ============ Events ============
    event CandidateAdded(uint256 indexed candidateId, string name);
    event VoteCast(address indexed voter, uint256 indexed candidateId, uint256 timestamp);
    event ElectionStarted(uint256 timestamp);
    event ElectionEnded(uint256 timestamp);

    // ============ Modifiers ============
    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }

    modifier electionIsActive() {
        require(electionActive, "Election is not currently active");
        _;
    }

    modifier electionNotActive() {
        require(!electionActive, "Election is currently active");
        _;
    }

    // ============ Constructor ============
    constructor() {
        admin = msg.sender;
        electionActive = false;
        candidateCount = 0;
    }

    // ============ Admin Functions ============

    /**
     * @dev Add a new candidate. Only admin can call this.
     * @param _name The name of the candidate
     */
    function addCandidate(string memory _name) public onlyAdmin {
        candidateCount++;
        candidates[candidateCount] = CandidateInfo(candidateCount, _name, 0);
        emit CandidateAdded(candidateCount, _name);
    }

    /**
     * @dev Start the election. Only admin can call this.
     */
    function startElection() public onlyAdmin electionNotActive {
        electionActive = true;
        emit ElectionStarted(block.timestamp);
    }

    /**
     * @dev End the election. Only admin can call this.
     */
    function endElection() public onlyAdmin electionIsActive {
        electionActive = false;
        emit ElectionEnded(block.timestamp);
    }

    // ============ Voter Functions ============

    /**
     * @dev Cast a vote for a candidate. Prevents double voting.
     * @param _candidateId The ID of the candidate to vote for
     */
    function vote(uint256 _candidateId) public electionIsActive {
        require(!hasVoted[msg.sender], "You have already voted");
        require(_candidateId > 0 && _candidateId <= candidateCount, "Invalid candidate ID");

        hasVoted[msg.sender] = true;
        candidates[_candidateId].voteCount++;

        emit VoteCast(msg.sender, _candidateId, block.timestamp);
    }

    // ============ View Functions ============

    /**
     * @dev Get candidate details by ID
     * @param _candidateId The candidate ID
     * @return id, name, voteCount
     */
    function getCandidate(uint256 _candidateId) public view returns (
        uint256 id,
        string memory name,
        uint256 voteCount
    ) {
        require(_candidateId > 0 && _candidateId <= candidateCount, "Invalid candidate ID");
        CandidateInfo memory c = candidates[_candidateId];
        return (c.id, c.name, c.voteCount);
    }

    /**
     * @dev Get total number of candidates
     * @return The candidate count
     */
    function getCandidateCount() public view returns (uint256) {
        return candidateCount;
    }

    /**
     * @dev Check if an address has already voted
     * @param _voter The voter address
     * @return Whether the address has voted
     */
    function hasVoterVoted(address _voter) public view returns (bool) {
        return hasVoted[_voter];
    }

    /**
     * @dev Check if election is currently active
     * @return Election active status
     */
    function isElectionActive() public view returns (bool) {
        return electionActive;
    }
}
