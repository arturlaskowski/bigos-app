package com.bigos.payment.adapters.payment.outbox.exception;

public class PaymentOutboxNotFoundException extends RuntimeException{

    public PaymentOutboxNotFoundException(String message) {
        super(message);
    }
}
