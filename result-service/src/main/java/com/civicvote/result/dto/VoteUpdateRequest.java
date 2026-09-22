package com.civicvote.result.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteUpdateRequest {
    @NotNull
    private Long electionId;
    @NotNull
    private Long candidateId;
    private int incrementBy;
    private String candidateName;
}
