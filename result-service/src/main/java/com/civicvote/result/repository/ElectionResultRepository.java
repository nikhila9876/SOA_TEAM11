package com.civicvote.result.repository;

import com.civicvote.result.entity.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ElectionResultRepository extends JpaRepository<ElectionResult, Long> {
    List<ElectionResult> findByElectionIdOrderByVoteCountDesc(Long electionId);
    Optional<ElectionResult> findByElectionIdAndCandidateId(Long electionId, Long candidateId);

    @Modifying
    @Query("UPDATE ElectionResult r SET r.voteCount = r.voteCount + :amount WHERE r.electionId = :electionId AND r.candidateId = :candidateId")
    int incrementVoteCount(@Param("electionId") Long electionId, @Param("candidateId") Long candidateId, @Param("amount") int amount);
}
