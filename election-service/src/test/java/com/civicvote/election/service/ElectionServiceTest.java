package com.civicvote.election.service;

import com.civicvote.election.dto.*;
import com.civicvote.election.entity.*;
import com.civicvote.election.exception.*;
import com.civicvote.election.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElectionServiceTest {

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private ElectionService electionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createElection_validRequest_returnsResponse() {
        ElectionRequest req = new ElectionRequest("Test Election", "Desc",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10));
        Election saved = Election.builder().id(1L).title("Test Election")
                .description("Desc").startDate(req.getStartDate()).endDate(req.getEndDate())
                .status(ElectionStatus.DRAFT).build();
        when(electionRepository.save(any())).thenReturn(saved);

        ElectionResponse resp = electionService.createElection(req);

        assertNotNull(resp);
        assertEquals("Test Election", resp.getTitle());
        assertEquals(ElectionStatus.DRAFT, resp.getStatus());
    }

    @Test
    void createElection_invalidDates_throwsException() {
        ElectionRequest req = new ElectionRequest("Test", "Desc",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> electionService.createElection(req));
    }

    @Test
    void getElectionById_notFound_throwsException() {
        when(electionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> electionService.getElectionById(99L));
    }

    @Test
    void activateElection_draftStatus_becomesActive() {
        Election election = Election.builder().id(1L).title("Test")
                .status(ElectionStatus.DRAFT).build();
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(electionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ElectionResponse resp = electionService.activateElection(1L);
        assertEquals(ElectionStatus.ACTIVE, resp.getStatus());
    }

    @Test
    void activateElection_closedStatus_throwsException() {
        Election election = Election.builder().id(1L).title("Test")
                .status(ElectionStatus.CLOSED).build();
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        assertThrows(IllegalStateException.class, () -> electionService.activateElection(1L));
    }

    @Test
    void closeElection_activeStatus_becomesClosed() {
        Election election = Election.builder().id(1L).title("Test")
                .status(ElectionStatus.ACTIVE).build();
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(electionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ElectionResponse resp = electionService.closeElection(1L);
        assertEquals(ElectionStatus.CLOSED, resp.getStatus());
    }

    @Test
    void addCandidate_closedElection_throwsException() {
        Election election = Election.builder().id(1L).title("Test")
                .status(ElectionStatus.CLOSED).build();
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        CandidateRequest req = new CandidateRequest("Candidate A", "Desc");
        assertThrows(ElectionClosedException.class, () -> electionService.addCandidate(1L, req));
    }
}
