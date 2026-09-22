package com.civicvote.voting.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateVoteUpdateRequest {
    private Long electionId;
    private Long candidateId;
    private int incrementBy;
}
