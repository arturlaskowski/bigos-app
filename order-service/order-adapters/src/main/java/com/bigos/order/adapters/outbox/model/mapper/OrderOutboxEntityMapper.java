package com.bigos.order.adapters.outbox.model.mapper;

import com.bigos.common.adapters.outbox.model.entity.mapper.OutboxEntityMapper;
import com.bigos.order.adapters.outbox.model.entity.OrderOutboxEntity;
import com.bigos.order.domain.ports.dto.outbox.OrderOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxEntityMapper extends OutboxEntityMapper<OrderOutboxEntity> {

    public <G extends OrderOutboxMessage> G orderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity outboxEntity, Class<G> outboxMessageClass) {
        G outboxMessage = super.outboxEntityToOutboxMessage(outboxEntity, outboxMessageClass);
        outboxMessage.setSagaStatus(outboxEntity.getSagaStatus());
        outboxMessage.setProcessedDate(outboxEntity.getProcessedDate());
        return outboxMessage;
    }

    public <G extends OrderOutboxMessage> OrderOutboxEntity orderOutboxMessageToOrderOutboxEntity(G outboxMessage) {
        OrderOutboxEntity orderOutboxEntity = super.outboxMessageToOutboxEntity(outboxMessage, OrderOutboxEntity.class);
        orderOutboxEntity.setMessageType(outboxMessage.getClass().getSimpleName());
        orderOutboxEntity.setSagaStatus(outboxMessage.getSagaStatus());
        orderOutboxEntity.setProcessedDate(outboxMessage.getProcessedDate());
        return orderOutboxEntity;
    }
}
