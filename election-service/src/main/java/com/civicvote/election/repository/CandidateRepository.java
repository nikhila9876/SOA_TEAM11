package com.civicvote.election.repository;

import com.civicvote.election.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElectionId(Long electionId);
    boolean existsByElectionIdAndId(Long electionId, Long candidateId);
}
