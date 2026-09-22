package com.civicvote.voting.exception;

public class ElectionClosedException extends RuntimeException {
    public ElectionClosedException(String message) { super(message); }
}
