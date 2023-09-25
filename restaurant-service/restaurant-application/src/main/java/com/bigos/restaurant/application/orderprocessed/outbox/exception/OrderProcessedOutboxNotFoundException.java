package com.bigos.restaurant.application.orderprocessed.outbox.exception;

public class OrderProcessedOutboxNotFoundException extends RuntimeException {
    public OrderProcessedOutboxNotFoundException(String message) {
        super(message);
    }
}
