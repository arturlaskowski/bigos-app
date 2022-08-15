package com.bigos.payment.domain.event;


import com.bigos.payment.domain.model.Payment;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PaymentRejectedEvent extends PaymentEvent {

    private final String failureMessages;

    public PaymentRejectedEvent(Payment payment, String failureMessages) {
        super(payment, Instant.now());
        this.failureMessages = failureMessages;
    }

}
