package com.bigos.restaurant.domain.ports.out.message;

import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;

import java.util.function.Consumer;

public interface OrderApprovalMessagePublisher {

    void publish(OrderProcessedOutboxMessage orderOutboxMessage, Consumer<OrderProcessedOutboxMessage> outboxCallback);
}
