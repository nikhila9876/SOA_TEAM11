package com.civicvote.election.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "candidates", indexes = {
    @Index(name = "idx_candidate_election", columnList = "election_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Column(nullable = false)
    @NotBlank(message = "Candidate name is required")
    @Size(min = 2, max = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
