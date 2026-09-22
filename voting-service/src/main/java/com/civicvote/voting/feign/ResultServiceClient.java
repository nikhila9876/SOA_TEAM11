package com.civicvote.voting.feign;

import com.civicvote.voting.dto.CandidateVoteUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "result-service", fallback = ResultServiceFallback.class)
public interface ResultServiceClient {

    @PostMapping("/api/results/update-vote")
    ResponseEntity<Void> updateVoteCount(@RequestBody CandidateVoteUpdateRequest request);
}
