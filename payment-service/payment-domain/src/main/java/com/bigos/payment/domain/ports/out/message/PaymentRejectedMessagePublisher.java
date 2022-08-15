package com.bigos.payment.domain.ports.out.message;

import com.bigos.common.domain.event.publisher.DomainEventPublisher;
import com.bigos.payment.domain.event.PaymentRejectedEvent;

public interface PaymentRejectedMessagePublisher extends DomainEventPublisher<PaymentRejectedEvent> {
}
