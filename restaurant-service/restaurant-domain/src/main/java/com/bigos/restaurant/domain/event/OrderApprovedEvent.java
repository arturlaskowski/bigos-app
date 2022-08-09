package com.bigos.restaurant.domain.event;

import com.bigos.restaurant.domain.model.OrderProcessed;

import java.time.Instant;

public class OrderApprovedEvent extends OrderProcessedEvent {

    public OrderApprovedEvent(OrderProcessed orderProcessed, Instant createdAt) {
        super(orderProcessed, createdAt);
    }

}
