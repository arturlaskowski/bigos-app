package com.bigos.restaurant.domain.event;

import com.bigos.common.domain.event.DomainEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class OrderProcessedEvent implements DomainEvent<OrderProcessed> {

    private final OrderProcessed orderProcessed;
    private final Instant createdAt;

    protected OrderProcessedEvent(OrderProcessed orderProcessed, Instant createdAt) {
        this.orderProcessed = orderProcessed;
        this.createdAt = createdAt;
    }
}
