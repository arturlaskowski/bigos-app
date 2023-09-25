package com.bigos.payment.domain.event;


import com.bigos.payment.domain.core.Payment;

import java.time.Instant;

public class PaymentCompletedEvent extends PaymentEvent {

    public PaymentCompletedEvent(Payment payment) {
        super(payment, Instant.now());
    }

}
