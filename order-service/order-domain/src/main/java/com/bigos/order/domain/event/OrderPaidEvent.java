package com.bigos.order.domain.event;


import com.bigos.order.domain.core.Order;

import java.time.Instant;

public class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(Order order, Instant createdAt) {
        super(order, createdAt);
    }
}
