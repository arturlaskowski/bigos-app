package com.bigos.payment.domain.event;

import com.bigos.common.domain.event.DomainEvent;
import com.bigos.payment.domain.core.Payment;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class PaymentEvent implements DomainEvent<Payment> {

    private final Payment payment;
    private final Instant createdAt;

    PaymentEvent(Payment payment, Instant createdAt) {
        this.payment = payment;
        this.createdAt = createdAt;
    }
}
