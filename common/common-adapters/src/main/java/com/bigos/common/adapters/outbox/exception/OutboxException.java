package com.bigos.common.adapters.outbox.exception;

public class OutboxException extends RuntimeException {

    public OutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
