package com.civicvote.election.controller;

import com.civicvote.election.dto.*;
import com.civicvote.election.service.ElectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
@Tag(name = "Elections", description = "Election management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping
    @Operation(summary = "Get all elections")
    public ResponseEntity<List<ElectionResponse>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get election by ID")
    public ResponseEntity<ElectionResponse> getElectionById(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new election (Admin only)")
    public ResponseEntity<ElectionResponse> createElection(@Valid @RequestBody ElectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(electionService.createElection(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an election (Admin only)")
    public ResponseEntity<ElectionResponse> updateElection(@PathVariable Long id, @Valid @RequestBody ElectionRequest request) {
        return ResponseEntity.ok(electionService.updateElection(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an election (Admin only)")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate an election (Admin only)")
    public ResponseEntity<ElectionResponse> activateElection(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.activateElection(id));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close an election (Admin only)")
    public ResponseEntity<ElectionResponse> closeElection(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.closeElection(id));
    }

    @GetMapping("/{id}/candidates")
    @Operation(summary = "Get candidates for an election")
    public ResponseEntity<List<CandidateResponse>> getCandidates(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getCandidates(id));
    }

    @PostMapping("/{id}/candidates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a candidate to an election (Admin only)")
    public ResponseEntity<CandidateResponse> addCandidate(@PathVariable Long id, @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(electionService.addCandidate(id, request));
    }
}
