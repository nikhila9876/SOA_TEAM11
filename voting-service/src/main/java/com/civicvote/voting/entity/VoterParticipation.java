package com.civicvote.voting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voter_participation",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_election_voter",
        columnNames = {"election_id", "voter_reference"}
    ),
    indexes = {
        @Index(name = "idx_participation_election", columnList = "election_id"),
        @Index(name = "idx_participation_voter", columnList = "voter_reference")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoterParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "voter_reference", nullable = false)
    private String voterReference;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    @PrePersist
    public void prePersist() {
        this.votedAt = LocalDateTime.now();
    }
}
