package com.bigos.payment.domain.event;


import com.bigos.payment.domain.core.Payment;
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
