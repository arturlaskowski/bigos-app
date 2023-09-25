package com.bigos.common.applciaiton.outbox.exception;

public class OutboxException extends RuntimeException {

    public OutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
