package com.bigos.payment.application.payment.exception;

public class PaymentApplicationException extends RuntimeException {

    public PaymentApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
