package com.bigos.order.domain.ports.out.message;

import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;

import java.util.function.Consumer;

public interface OrderCreatedEventPublisher {
    void publish(OrderCreatedOutboxMessage message, Consumer<OrderCreatedOutboxMessage> outboxCallback);

}
