package com.civicvote.election.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateRequest {

    @NotBlank(message = "Candidate name is required")
    @Size(min = 2, max = 100)
    private String name;

    private String description;
}
