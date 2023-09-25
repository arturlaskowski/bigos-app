package com.bigos.order.domain.event;


import com.bigos.order.domain.core.Order;

import java.time.Instant;

public class OrderCreatedEvent extends OrderEvent {

    public OrderCreatedEvent(Order order, Instant createdAt) {
        super(order, createdAt);
    }
}
