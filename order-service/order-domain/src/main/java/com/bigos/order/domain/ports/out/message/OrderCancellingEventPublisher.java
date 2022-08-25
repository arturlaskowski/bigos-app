package com.bigos.order.domain.ports.out.message;


import com.bigos.order.domain.ports.dto.outbox.OrderCancellingOutboxMessage;

import java.util.function.Consumer;

public interface OrderCancellingEventPublisher {
    void publish(OrderCancellingOutboxMessage message, Consumer<OrderCancellingOutboxMessage> outboxCallback);

}
