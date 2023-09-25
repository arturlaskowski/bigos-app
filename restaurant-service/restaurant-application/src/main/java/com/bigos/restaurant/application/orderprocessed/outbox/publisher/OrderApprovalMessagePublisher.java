package com.bigos.restaurant.application.orderprocessed.outbox.publisher;

import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;

import java.util.function.Consumer;

public interface OrderApprovalMessagePublisher {

    void publish(OrderProcessedOutboxMessage orderOutboxMessage, Consumer<OrderProcessedOutboxMessage> outboxCallback);
}
