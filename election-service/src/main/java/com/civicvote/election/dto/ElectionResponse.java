package com.civicvote.election.dto;

import com.civicvote.election.entity.ElectionStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ElectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CandidateResponse> candidates;
}
