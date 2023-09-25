package com.bigos.order.application.outbox.publisher;

import com.bigos.common.applciaiton.outbox.OutboxPublisher;
import com.bigos.order.application.outbox.dto.OrderCreatedOutboxMessage;

public interface OrderCreatedEventPublisher extends OutboxPublisher<OrderCreatedOutboxMessage> {
}
