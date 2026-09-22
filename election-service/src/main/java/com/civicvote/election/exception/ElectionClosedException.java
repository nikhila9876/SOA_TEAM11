package com.civicvote.election.exception;

public class ElectionClosedException extends RuntimeException {
    public ElectionClosedException(String message) {
        super(message);
    }
}
