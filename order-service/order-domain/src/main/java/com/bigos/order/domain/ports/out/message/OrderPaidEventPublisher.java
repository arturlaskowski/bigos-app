package com.bigos.order.domain.ports.out.message;


import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;

import java.util.function.Consumer;

public interface OrderPaidEventPublisher {
    void publish(OrderPaidOutboxMessage message, Consumer<OrderPaidOutboxMessage> outboxCallback);

}
