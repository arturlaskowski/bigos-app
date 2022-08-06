package com.bigos.payment.domain.event;


import com.bigos.payment.domain.model.Payment;

import java.time.Instant;

public class PaymentCancelledEvent extends PaymentEvent {

    public PaymentCancelledEvent(Payment payment) {
        super(payment, Instant.now());
    }

}
