package com.bigos.restaurant.adapters.orderprocessed.outbox.exception;

public class OrderProcessedOutboxNotFoundException extends RuntimeException {
    public OrderProcessedOutboxNotFoundException(String message) {
        super(message);
    }
}
