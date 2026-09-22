package com.civicvote.result.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultSummaryResponse {
    private Long electionId;
    private int totalVotes;
    private String winner;
    private Long winnerVotes;
    private List<ResultResponse> results;
}
