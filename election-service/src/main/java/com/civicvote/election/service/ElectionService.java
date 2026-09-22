package com.civicvote.election.service;

import com.civicvote.election.dto.*;
import com.civicvote.election.entity.*;
import com.civicvote.election.exception.*;
import com.civicvote.election.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    public List<ElectionResponse> getAllElections() {
        return electionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ElectionResponse getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        return mapToResponse(election);
    }

    public ElectionResponse createElection(ElectionRequest request) {
        validateDates(request);
        Election election = Election.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ElectionStatus.DRAFT)
                .build();
        return mapToResponse(electionRepository.save(election));
    }

    public ElectionResponse updateElection(Long id, ElectionRequest request) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        if (election.getStatus() == ElectionStatus.CLOSED) {
            throw new ElectionClosedException("Cannot update a closed election");
        }
        validateDates(request);
        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartDate(request.getStartDate());
        election.setEndDate(request.getEndDate());
        return mapToResponse(electionRepository.save(election));
    }

    public void deleteElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        if (election.getStatus() == ElectionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active election");
        }
        electionRepository.delete(election);
    }

    public ElectionResponse activateElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        if (election.getStatus() != ElectionStatus.DRAFT && election.getStatus() != ElectionStatus.SCHEDULED) {
            throw new IllegalStateException("Only DRAFT or SCHEDULED elections can be activated");
        }
        election.setStatus(ElectionStatus.ACTIVE);
        return mapToResponse(electionRepository.save(election));
    }

    public ElectionResponse closeElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        if (election.getStatus() != ElectionStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE elections can be closed");
        }
        election.setStatus(ElectionStatus.CLOSED);
        return mapToResponse(electionRepository.save(election));
    }

    public List<CandidateResponse> getCandidates(Long electionId) {
        electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));
        return candidateRepository.findByElectionId(electionId).stream()
                .map(this::mapCandidateToResponse)
                .collect(Collectors.toList());
    }

    public CandidateResponse addCandidate(Long electionId, CandidateRequest request) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));
        if (election.getStatus() == ElectionStatus.CLOSED) {
            throw new ElectionClosedException("Cannot add candidates to a closed election");
        }
        Candidate candidate = Candidate.builder()
                .election(election)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapCandidateToResponse(candidateRepository.save(candidate));
    }

    private void validateDates(ElectionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    private ElectionResponse mapToResponse(Election election) {
        List<CandidateResponse> candidates = election.getCandidates().stream()
                .map(this::mapCandidateToResponse)
                .collect(Collectors.toList());
        return ElectionResponse.builder()
                .id(election.getId())
                .title(election.getTitle())
                .description(election.getDescription())
                .startDate(election.getStartDate())
                .endDate(election.getEndDate())
                .status(election.getStatus())
                .createdAt(election.getCreatedAt())
                .updatedAt(election.getUpdatedAt())
                .candidates(candidates)
                .build();
    }

    private CandidateResponse mapCandidateToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .electionId(candidate.getElection().getId())
                .name(candidate.getName())
                .description(candidate.getDescription())
                .build();
    }
}
