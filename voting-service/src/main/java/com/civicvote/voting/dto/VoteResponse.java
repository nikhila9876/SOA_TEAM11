package com.civicvote.voting.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {
    private Long voteId;
    private Long electionId;
    private LocalDateTime castAt;
    private String integrityHash;
    private String message;
}
