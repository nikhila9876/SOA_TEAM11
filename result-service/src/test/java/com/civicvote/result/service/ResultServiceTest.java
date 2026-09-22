package com.civicvote.result.service;

import com.civicvote.result.dto.*;
import com.civicvote.result.entity.ElectionResult;
import com.civicvote.result.repository.ElectionResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultServiceTest {

    @Mock private ElectionResultRepository resultRepository;
    @InjectMocks private ResultService resultService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getResultsByElection_returnsOrderedResults() {
        List<ElectionResult> results = List.of(
            ElectionResult.builder().id(1L).electionId(1L).candidateId(1L).candidateName("A").voteCount(50L).build(),
            ElectionResult.builder().id(2L).electionId(1L).candidateId(2L).candidateName("B").voteCount(30L).build()
        );
        when(resultRepository.findByElectionIdOrderByVoteCountDesc(1L)).thenReturn(results);

        List<ResultResponse> resp = resultService.getResultsByElection(1L);
        assertEquals(2, resp.size());
        assertEquals(50L, resp.get(0).getVoteCount());
    }

    @Test
    void getSummary_returnsCorrectWinner() {
        List<ElectionResult> results = List.of(
            ElectionResult.builder().id(1L).electionId(1L).candidateId(1L).candidateName("Alice").voteCount(60L).build(),
            ElectionResult.builder().id(2L).electionId(1L).candidateId(2L).candidateName("Bob").voteCount(40L).build()
        );
        when(resultRepository.findByElectionIdOrderByVoteCountDesc(1L)).thenReturn(results);

        ResultSummaryResponse summary = resultService.getSummary(1L);
        assertEquals("Alice", summary.getWinner());
        assertEquals(100, summary.getTotalVotes());
        assertEquals(60L, summary.getWinnerVotes());
    }

    @Test
    void getSummary_emptyElection_noWinner() {
        when(resultRepository.findByElectionIdOrderByVoteCountDesc(1L)).thenReturn(List.of());
        ResultSummaryResponse summary = resultService.getSummary(1L);
        assertEquals("No votes yet", summary.getWinner());
        assertEquals(0, summary.getTotalVotes());
    }

    @Test
    void updateVoteCount_existingCandidate_increments() {
        VoteUpdateRequest req = new VoteUpdateRequest(1L, 1L, 1, "Alice");
        when(resultRepository.incrementVoteCount(1L, 1L, 1)).thenReturn(1);
        resultService.updateVoteCount(req);
        verify(resultRepository, times(1)).incrementVoteCount(1L, 1L, 1);
        verify(resultRepository, never()).save(any());
    }

    @Test
    void updateVoteCount_newCandidate_createsRecord() {
        VoteUpdateRequest req = new VoteUpdateRequest(1L, 1L, 1, "Alice");
        when(resultRepository.incrementVoteCount(1L, 1L, 1)).thenReturn(0);
        when(resultRepository.save(any())).thenReturn(new ElectionResult());
        resultService.updateVoteCount(req);
        verify(resultRepository, times(1)).save(any());
    }
}
