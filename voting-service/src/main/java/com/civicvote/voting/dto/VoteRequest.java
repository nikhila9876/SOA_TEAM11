package com.civicvote.voting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {

    @NotNull(message = "Election ID is required")
    private Long electionId;

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
}
