package com.bigos.restaurant.domain.event;

import com.bigos.restaurant.domain.model.OrderProcessed;

import java.time.Instant;

public class OrderAcceptedEvent extends OrderProcessedEvent {

    public OrderAcceptedEvent(OrderProcessed orderProcessed, Instant createdAt) {
        super(orderProcessed, createdAt);
    }

}
