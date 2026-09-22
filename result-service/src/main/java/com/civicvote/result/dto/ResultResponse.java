package com.civicvote.result.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultResponse {
    private Long id;
    private Long electionId;
    private Long candidateId;
    private String candidateName;
    private Long voteCount;
    private LocalDateTime calculatedAt;
}
