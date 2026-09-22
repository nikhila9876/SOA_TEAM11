package com.civicvote.voting.repository;

import com.civicvote.voting.entity.VoterParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoterParticipationRepository extends JpaRepository<VoterParticipation, Long> {
    boolean existsByElectionIdAndVoterReference(Long electionId, String voterReference);
    Optional<VoterParticipation> findByElectionIdAndVoterReference(Long electionId, String voterReference);
}
