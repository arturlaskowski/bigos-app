package com.bigos.restaurant.entities.orderprocessed.outbox;

import com.bigos.common.entities.outbox.OutboxEntityMapper;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessedOutboxEntityMapper extends OutboxEntityMapper<OrderProcessedOutboxEntity> {

    public <G extends OrderProcessedOutboxMessage> G orderProcessedOutboxEntityToOrderProcessedOutboxMessage(OrderProcessedOutboxEntity outboxEntity, Class<G> outboxMessageClass) {
        G outboxMessage = super.outboxEntityToOutboxMessage(outboxEntity, outboxMessageClass);
        outboxMessage.setApprovalStatus(outboxEntity.getApprovalStatus());
        return outboxMessage;
    }

    public OrderProcessedOutboxEntity orderProcessedOutboxMessageToOrderProcessedOutboxEntity(OrderProcessedOutboxMessage outboxMessage) {
        OrderProcessedOutboxEntity orderProcessedOutboxEntity = super.outboxMessageToOutboxEntity(outboxMessage, OrderProcessedOutboxEntity.class);
        orderProcessedOutboxEntity.setMessageType(OrderProcessedOutboxMessage.class.getSimpleName());
        orderProcessedOutboxEntity.setApprovalStatus(outboxMessage.getApprovalStatus());
        return orderProcessedOutboxEntity;
    }
}
