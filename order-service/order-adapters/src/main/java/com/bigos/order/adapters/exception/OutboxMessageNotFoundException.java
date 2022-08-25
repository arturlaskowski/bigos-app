package com.bigos.order.adapters.exception;

public class OutboxMessageNotFoundException extends RuntimeException {

    public OutboxMessageNotFoundException(String message) {
        super(message);
    }
}
