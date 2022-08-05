package com.bigos.order.domain.event;


import com.bigos.order.domain.model.Order;

import java.time.Instant;

public class OrderCancellingEvent extends OrderEvent {
    public OrderCancellingEvent(Order order,
                                Instant createdAt) {
        super(order, createdAt);
    }
}
