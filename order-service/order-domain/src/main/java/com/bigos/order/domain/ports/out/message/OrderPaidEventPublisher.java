package com.bigos.order.domain.ports.out.message;


import com.bigos.common.domain.event.publisher.DomainEventPublisher;
import com.bigos.order.domain.event.OrderPaidEvent;

public interface OrderPaidEventPublisher extends DomainEventPublisher<OrderPaidEvent> {
}
