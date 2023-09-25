package com.bigos.payment.application.payment.exception;

public class PaymentOutboxNotFoundException extends RuntimeException{

    public PaymentOutboxNotFoundException(String message) {
        super(message);
    }
}
