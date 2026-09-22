package com.civicvote.voting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ballots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ballot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "cast_at", nullable = false)
    private LocalDateTime castAt;

    @Column(name = "integrity_hash", nullable = false, length = 64)
    private String integrityHash;

    @PrePersist
    public void prePersist() {
        this.castAt = LocalDateTime.now();
    }
}
