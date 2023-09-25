package com.bigos.order.application.outbox.publisher;

import com.bigos.common.applciaiton.outbox.OutboxPublisher;
import com.bigos.order.application.outbox.dto.OrderPaidOutboxMessage;

public interface OrderPaidEventPublisher extends OutboxPublisher<OrderPaidOutboxMessage> {
}
