package com.civicvote.result.service;

import com.civicvote.result.dto.*;
import com.civicvote.result.entity.ElectionResult;
import com.civicvote.result.repository.ElectionResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResultService {

    private final ElectionResultRepository resultRepository;

    public List<ResultResponse> getResultsByElection(Long electionId) {
        return resultRepository.findByElectionIdOrderByVoteCountDesc(electionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResultSummaryResponse getSummary(Long electionId) {
        List<ElectionResult> results = resultRepository.findByElectionIdOrderByVoteCountDesc(electionId);
        int totalVotes = results.stream().mapToInt(r -> r.getVoteCount().intValue()).sum();
        String winner = results.isEmpty() ? "No votes yet" : results.get(0).getCandidateName();
        Long winnerVotes = results.isEmpty() ? 0L : results.get(0).getVoteCount();

        return ResultSummaryResponse.builder()
                .electionId(electionId)
                .totalVotes(totalVotes)
                .winner(winner)
                .winnerVotes(winnerVotes)
                .results(results.stream().map(this::mapToResponse).collect(Collectors.toList()))
                .build();
    }

    public List<ResultResponse> calculateResults(Long electionId) {
        // Results are stored incrementally via vote updates from Voting Service
        // This endpoint triggers a fresh read/recalculation from stored data
        List<ElectionResult> results = resultRepository.findByElectionIdOrderByVoteCountDesc(electionId);
        log.info("Calculated results for election {}: {} candidates", electionId, results.size());
        return results.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void updateVoteCount(VoteUpdateRequest request) {
        int updated = resultRepository.incrementVoteCount(
                request.getElectionId(), request.getCandidateId(), request.getIncrementBy());

        if (updated == 0) {
            // First vote for this candidate in this election - create new record
            ElectionResult result = ElectionResult.builder()
                    .electionId(request.getElectionId())
                    .candidateId(request.getCandidateId())
                    .candidateName(request.getCandidateName() != null ? request.getCandidateName() : "Candidate " + request.getCandidateId())
                    .voteCount((long) request.getIncrementBy())
                    .build();
            resultRepository.save(result);
            log.info("Created new result record for election {} candidate {}", request.getElectionId(), request.getCandidateId());
        } else {
            log.info("Updated vote count for election {} candidate {}", request.getElectionId(), request.getCandidateId());
        }
    }

    private ResultResponse mapToResponse(ElectionResult result) {
        return ResultResponse.builder()
                .id(result.getId())
                .electionId(result.getElectionId())
                .candidateId(result.getCandidateId())
                .candidateName(result.getCandidateName())
                .voteCount(result.getVoteCount())
                .calculatedAt(result.getCalculatedAt())
                .build();
    }
}
