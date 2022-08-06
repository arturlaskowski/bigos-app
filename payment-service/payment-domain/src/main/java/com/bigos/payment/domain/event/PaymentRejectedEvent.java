package com.bigos.payment.domain.event;


import com.bigos.payment.domain.model.Payment;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PaymentRejectedEvent extends PaymentEvent {

    private final String failureMessage;

    public PaymentRejectedEvent(Payment payment, String failureMessage) {
        super(payment, Instant.now());
        this.failureMessage = failureMessage;
    }

}
