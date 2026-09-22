package com.civicvote.voting.repository;

import com.civicvote.voting.entity.Ballot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BallotRepository extends JpaRepository<Ballot, Long> {
    List<Ballot> findByElectionId(Long electionId);

    @Query("SELECT COUNT(b) FROM Ballot b WHERE b.electionId = :electionId AND b.candidateId = :candidateId")
    Long countByCandidateIdAndElectionId(Long electionId, Long candidateId);
}
