package com.civicvote.result.controller;

import com.civicvote.result.dto.*;
import com.civicvote.result.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Election result APIs")
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/{electionId}")
    @Operation(summary = "Get results for an election")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<ResultResponse>> getResults(@PathVariable Long electionId) {
        return ResponseEntity.ok(resultService.getResultsByElection(electionId));
    }

    @GetMapping("/{electionId}/summary")
    @Operation(summary = "Get result summary with winner for an election")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ResultSummaryResponse> getSummary(@PathVariable Long electionId) {
        return ResponseEntity.ok(resultService.getSummary(electionId));
    }

    @PostMapping("/{electionId}/calculate")
    @Operation(summary = "Trigger result calculation (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<ResultResponse>> calculateResults(@PathVariable Long electionId) {
        return ResponseEntity.ok(resultService.calculateResults(electionId));
    }

    @PostMapping("/update-vote")
    @Operation(summary = "Internal: Update vote count (called by Voting Service via Feign)")
    public ResponseEntity<Void> updateVoteCount(@RequestBody VoteUpdateRequest request) {
        resultService.updateVoteCount(request);
        return ResponseEntity.ok().build();
    }
}
