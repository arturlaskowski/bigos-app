package com.bigos.payment.adapters.payment.exception;

public class PaymentApplicationException extends RuntimeException {

    public PaymentApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
