package com.votingsystem.repository;

import com.votingsystem.entity.VoteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VoteLogRepository extends JpaRepository<VoteLog, Long> {
    boolean existsByVoterWallet(String voterWallet);

    @Query("SELECT v.candidateId, COUNT(v) FROM VoteLog v WHERE v.candidateId IN (SELECT c.id FROM Candidate c WHERE c.election.id = :electionId) GROUP BY v.candidateId")
    List<Object[]> countVotesByElectionId(@Param("electionId") Long electionId);

    @Query("SELECT COUNT(v) FROM VoteLog v WHERE v.candidateId IN (SELECT c.id FROM Candidate c WHERE c.election.id = :electionId)")
    long countByElectionId(@Param("electionId") Long electionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VoteLog v WHERE v.candidateId IN (SELECT c.id FROM Candidate c WHERE c.election.id = :electionId)")
    void deleteByElectionId(@Param("electionId") Long electionId);
}
