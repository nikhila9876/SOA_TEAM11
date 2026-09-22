package com.civicvote.voting.service;

import com.civicvote.voting.dto.*;
import com.civicvote.voting.entity.*;
import com.civicvote.voting.exception.*;
import com.civicvote.voting.feign.ResultServiceClient;
import com.civicvote.voting.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VotingServiceTest {

    @Mock private BallotRepository ballotRepository;
    @Mock private VoterParticipationRepository participationRepository;
    @Mock private AuditRepository auditRepository;
    @Mock private HashService hashService;
    @Mock private ResultServiceClient resultServiceClient;

    @InjectMocks private VotingService votingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void castVote_validRequest_returnsResponse() {
        VoteRequest req = new VoteRequest(1L, 2L);
        when(participationRepository.existsByElectionIdAndVoterReference(1L, "voter1")).thenReturn(false);
        when(hashService.computeBallotHash(anyLong(), anyLong(), anyString())).thenReturn("abc123hash");
        Ballot savedBallot = Ballot.builder().voteId(1L).electionId(1L).candidateId(2L)
                .integrityHash("abc123hash").build();
        when(ballotRepository.save(any())).thenReturn(savedBallot);
        when(participationRepository.save(any())).thenReturn(new VoterParticipation());
        when(auditRepository.save(any())).thenReturn(new AuditRecord());
        when(resultServiceClient.updateVoteCount(any())).thenReturn(ResponseEntity.ok().build());

        VoteResponse resp = votingService.castVote(req, 1L, "voter1");

        assertNotNull(resp);
        assertEquals(1L, resp.getElectionId());
        assertNotNull(resp.getMessage());
    }

    @Test
    void castVote_duplicateVote_throwsException() {
        VoteRequest req = new VoteRequest(1L, 2L);
        when(participationRepository.existsByElectionIdAndVoterReference(1L, "voter1")).thenReturn(true);
        assertThrows(DuplicateVoteException.class, () -> votingService.castVote(req, 1L, "voter1"));
    }

    @Test
    void getVoteStatus_voted_returnsTrue() {
        when(participationRepository.existsByElectionIdAndVoterReference(1L, "voter1")).thenReturn(true);
        VoteStatusResponse resp = votingService.getVoteStatus(1L, "voter1");
        assertTrue(resp.isHasVoted());
    }

    @Test
    void getVoteStatus_notVoted_returnsFalse() {
        when(participationRepository.existsByElectionIdAndVoterReference(1L, "voter1")).thenReturn(false);
        VoteStatusResponse resp = votingService.getVoteStatus(1L, "voter1");
        assertFalse(resp.isHasVoted());
    }

    @Test
    void verifyAudit_allValid_returnsValid() {
        when(ballotRepository.findAll()).thenReturn(java.util.List.of(
            Ballot.builder().voteId(1L).integrityHash("a".repeat(64)).build()
        ));
        AuditVerificationResponse resp = votingService.verifyAuditIntegrity();
        assertEquals(1, resp.getTotalRecordsChecked());
        assertEquals("VALID", resp.getIntegrityStatus());
    }
}
