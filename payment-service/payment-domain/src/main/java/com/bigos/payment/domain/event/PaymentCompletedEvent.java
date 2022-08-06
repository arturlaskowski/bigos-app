package com.bigos.payment.domain.event;


import com.bigos.payment.domain.model.Payment;

import java.time.Instant;

public class PaymentCompletedEvent extends PaymentEvent {

    public PaymentCompletedEvent(Payment payment) {
        super(payment, Instant.now());
    }

}
