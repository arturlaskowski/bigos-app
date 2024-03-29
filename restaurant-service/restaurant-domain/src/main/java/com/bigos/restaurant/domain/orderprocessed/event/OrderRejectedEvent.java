package com.bigos.restaurant.domain.orderprocessed.event;

import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import lombok.Getter;

import java.time.Instant;

@Getter
public class OrderRejectedEvent extends OrderProcessedEvent {

    private final String failureMessages;

    public OrderRejectedEvent(OrderProcessed orderProcessed, Instant createdAt, String failureMessages) {
        super(orderProcessed, createdAt);
        this.failureMessages = failureMessages;
    }

}
