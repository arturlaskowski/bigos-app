package com.bigos.order.domain.ports.out.message;


import com.bigos.common.domain.event.publisher.DomainEventPublisher;
import com.bigos.order.domain.event.OrderCancellingEvent;

public interface OrderCancellingEventPublisher extends DomainEventPublisher<OrderCancellingEvent> {
}
