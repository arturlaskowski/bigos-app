package com.bigos.restaurant.domain.ports.out.message;

import com.bigos.common.domain.event.publisher.DomainEventPublisher;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
