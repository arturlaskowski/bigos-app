package com.bigos.restaurant.domain.event;

import com.bigos.restaurant.domain.model.OrderProcessed;
import lombok.Getter;

import java.time.Instant;

@Getter
public class OrderRejectedEvent extends OrderProcessedEvent {

    private final String failureMessage;

    public OrderRejectedEvent(OrderProcessed orderProcessed, Instant createdAt, String failureMessage) {
        super(orderProcessed, createdAt);
        this.failureMessage = failureMessage;
    }

}
