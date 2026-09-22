package com.civicvote.election.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private Long id;
    private Long electionId;
    private String name;
    private String description;
}
