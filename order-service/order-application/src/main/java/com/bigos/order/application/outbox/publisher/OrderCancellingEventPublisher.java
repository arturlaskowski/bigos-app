package com.bigos.order.application.outbox.publisher;

import com.bigos.common.applciaiton.outbox.OutboxPublisher;
import com.bigos.order.application.outbox.dto.OrderCancellingOutboxMessage;

public interface OrderCancellingEventPublisher extends OutboxPublisher<OrderCancellingOutboxMessage> {
}
