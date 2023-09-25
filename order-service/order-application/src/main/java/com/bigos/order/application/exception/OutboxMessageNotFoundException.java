package com.bigos.order.application.exception;

public class OutboxMessageNotFoundException extends RuntimeException {

    public OutboxMessageNotFoundException(String message) {
        super(message);
    }
}
