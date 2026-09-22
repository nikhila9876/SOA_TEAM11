package com.civicvote.voting.controller;

import com.civicvote.voting.dto.*;
import com.civicvote.voting.service.VotingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "Voting", description = "Vote casting and auditing APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class VotingController {

    private final VotingService votingService;

    @PostMapping
    @Operation(summary = "Cast a vote (authenticated voters)")
    public ResponseEntity<VoteResponse> castVote(@Valid @RequestBody VoteRequest request,
                                                  Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(votingService.castVote(request, userId, username));
    }

    @GetMapping("/status/{electionId}")
    @Operation(summary = "Check if the current user has voted in an election")
    public ResponseEntity<VoteStatusResponse> getVoteStatus(@PathVariable Long electionId,
                                                             Authentication authentication) {
        return ResponseEntity.ok(votingService.getVoteStatus(electionId, authentication.getName()));
    }

    @GetMapping("/audit/verify")
    @Operation(summary = "Verify audit integrity (Admin only)")
    public ResponseEntity<AuditVerificationResponse> verifyAudit() {
        return ResponseEntity.ok(votingService.verifyAuditIntegrity());
    }
}
