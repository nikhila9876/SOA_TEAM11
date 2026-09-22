package com.civicvote.voting.feign;

import com.civicvote.voting.dto.CandidateVoteUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResultServiceFallback implements ResultServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ResultServiceFallback.class);

    @Override
    public ResponseEntity<Void> updateVoteCount(CandidateVoteUpdateRequest request) {
        log.error("Result service unavailable, failed to update vote count for election: {}", request.getElectionId());
        return ResponseEntity.ok().build();
    }
}
