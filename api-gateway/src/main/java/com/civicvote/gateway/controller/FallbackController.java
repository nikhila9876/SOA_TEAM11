package com.civicvote.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 503,
                "error", "SERVICE_UNAVAILABLE",
                "message", "The service is temporarily unavailable. Please try again later."
        ));
    }
}
