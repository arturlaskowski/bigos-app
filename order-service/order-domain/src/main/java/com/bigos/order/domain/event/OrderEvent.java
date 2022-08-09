package com.bigos.order.domain.event;


import com.bigos.common.domain.event.DomainEvent;
import com.bigos.order.domain.model.Order;

import java.time.Instant;

public abstract class OrderEvent implements DomainEvent<Order> {
    private final Order order;
    private final Instant createdAt;

    OrderEvent(Order order, Instant createdAt) {
        this.order = order;
        this.createdAt = createdAt;
    }

    public Order getOrder() {
        return order;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
