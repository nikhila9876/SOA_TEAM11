package com.civicvote.result.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "election_results",
    indexes = {
        @Index(name = "idx_result_election", columnList = "election_id"),
        @Index(name = "idx_result_candidate", columnList = "candidate_id")
    },
    uniqueConstraints = @UniqueConstraint(name = "uk_election_candidate", columnNames = {"election_id", "candidate_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "vote_count", nullable = false)
    @Builder.Default
    private Long voteCount = 0L;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.calculatedAt = LocalDateTime.now();
    }
}
