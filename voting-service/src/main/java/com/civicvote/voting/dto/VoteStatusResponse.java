package com.civicvote.voting.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteStatusResponse {
    private Long electionId;
    private boolean hasVoted;
    private String message;
}
