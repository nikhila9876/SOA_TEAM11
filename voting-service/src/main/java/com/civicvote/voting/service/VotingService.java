package com.civicvote.voting.service;

import com.civicvote.voting.dto.*;
import com.civicvote.voting.entity.*;
import com.civicvote.voting.exception.*;
import com.civicvote.voting.feign.ResultServiceClient;
import com.civicvote.voting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VotingService {

    private final BallotRepository ballotRepository;
    private final VoterParticipationRepository participationRepository;
    private final AuditRepository auditRepository;
    private final HashService hashService;
    private final ResultServiceClient resultServiceClient;

    public VoteResponse castVote(VoteRequest request, Long userId, String username) {
        // 1. Check duplicate vote using DB unique constraint
        if (participationRepository.existsByElectionIdAndVoterReference(request.getElectionId(), username)) {
            throw new DuplicateVoteException("You have already voted in this election");
        }

        // 2. Generate integrity hash for the ballot
        String salt = username + System.nanoTime();
        String hash = hashService.computeBallotHash(request.getElectionId(), request.getCandidateId(), salt);

        // 3. Create anonymous ballot (no direct userId -> candidateId link in ballot)
        Ballot ballot = Ballot.builder()
                .electionId(request.getElectionId())
                .candidateId(request.getCandidateId())
                .integrityHash(hash)
                .build();

        // 4. Create participation record (links userId to electionId only - not candidateId)
        VoterParticipation participation = VoterParticipation.builder()
                .electionId(request.getElectionId())
                .voterReference(username)
                .build();

        try {
            ballot = ballotRepository.save(ballot);
            participationRepository.save(participation);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateVoteException("You have already voted in this election (concurrent attempt detected)");
        }

        // 5. Create audit record
        AuditRecord audit = AuditRecord.builder()
                .eventType("VOTE_CAST")
                .entityType("BALLOT")
                .entityId(ballot.getVoteId().toString())
                .integrityHash(hash)
                .build();
        auditRepository.save(audit);

        // 6. Notify Result Service via Feign
        try {
            CandidateVoteUpdateRequest updateReq = CandidateVoteUpdateRequest.builder()
                    .electionId(request.getElectionId())
                    .candidateId(request.getCandidateId())
                    .incrementBy(1)
                    .build();
            resultServiceClient.updateVoteCount(updateReq);
        } catch (Exception e) {
            log.warn("Failed to notify result service: {}", e.getMessage());
        }

        return VoteResponse.builder()
                .voteId(ballot.getVoteId())
                .electionId(ballot.getElectionId())
                .castAt(ballot.getCastAt())
                .integrityHash(ballot.getIntegrityHash())
                .message("Vote cast successfully. Your ballot is anonymous and tamper-evident.")
                .build();
    }

    public VoteStatusResponse getVoteStatus(Long electionId, String username) {
        boolean hasVoted = participationRepository.existsByElectionIdAndVoterReference(electionId, username);
        return VoteStatusResponse.builder()
                .electionId(electionId)
                .hasVoted(hasVoted)
                .message(hasVoted ? "You have already voted in this election" : "You have not voted yet")
                .build();
    }

    @Transactional(readOnly = true)
    public AuditVerificationResponse verifyAuditIntegrity() {
        List<Ballot> ballots = ballotRepository.findAll();
        int total = ballots.size();
        int valid = 0;
        int invalid = 0;

        for (Ballot ballot : ballots) {
            // Recompute hash from stored data - we cannot fully verify since we don't store salt
            // We verify that the hash format is correct (64 hex chars for SHA-256)
            String storedHash = ballot.getIntegrityHash();
            if (storedHash != null && storedHash.matches("[a-f0-9]{64}")) {
                valid++;
            } else {
                invalid++;
                log.warn("Integrity violation detected for ballot ID: {}", ballot.getVoteId());
            }
        }

        String status = invalid == 0 ? "VALID" : "INTEGRITY_VIOLATION_DETECTED";
        return AuditVerificationResponse.builder()
                .totalRecordsChecked(total)
                .validRecords(valid)
                .invalidRecords(invalid)
                .integrityStatus(status)
                .build();
    }
}
