package com.bigos.restaurant.domain.orderprocessed.event;

import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;

import java.time.Instant;

public class OrderAcceptedEvent extends OrderProcessedEvent {

    public OrderAcceptedEvent(OrderProcessed orderProcessed, Instant createdAt) {
        super(orderProcessed, createdAt);
    }

}
