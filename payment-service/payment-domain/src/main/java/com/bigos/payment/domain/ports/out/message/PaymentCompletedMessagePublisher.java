package com.bigos.payment.domain.ports.out.message;


import com.bigos.common.domain.event.publisher.DomainEventPublisher;
import com.bigos.payment.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedMessagePublisher extends DomainEventPublisher<PaymentCompletedEvent> {
}
