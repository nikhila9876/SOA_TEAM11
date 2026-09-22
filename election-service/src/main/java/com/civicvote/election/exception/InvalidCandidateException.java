package com.civicvote.election.exception;

public class InvalidCandidateException extends RuntimeException {
    public InvalidCandidateException(String message) {
        super(message);
    }
}
