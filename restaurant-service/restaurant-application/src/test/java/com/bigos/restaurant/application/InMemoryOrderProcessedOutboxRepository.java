package com.bigos.restaurant.application;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;
import com.bigos.restaurant.application.orderprocessed.outbox.repository.OrderProcessedOutboxRepository;

import java.util.*;
import java.util.stream.Collectors;

class InMemoryOrderProcessedOutboxRepository implements OrderProcessedOutboxRepository {

    private final Map<UUID, OrderProcessedOutboxMessage> store = new HashMap<>();

    @Override
    public <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage) {
        store.put(orderOutboxMessage.getId(), orderOutboxMessage);
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        return store.values().stream()
                .filter(outboxMessageClass::isInstance)
                .map(outboxMessageClass::cast)
                .filter(outboxMessage -> outboxMessage.getOutboxStatus().equals(outboxStatus))
                .collect(Collectors.toList());
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClass, UUID sagaId, OutboxStatus outboxStatus) {
        return store.values().stream()
                .anyMatch(outboxMessage -> outboxMessageClass.isInstance(outboxMessage)
                        && Objects.equals(outboxMessage.getSagaId(), sagaId)
                        && Objects.equals(outboxMessage.getOutboxStatus(), outboxStatus));
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        store.values().removeIf(outboxMessage -> outboxMessageClass.isInstance(outboxMessage) && outboxMessage.getOutboxStatus().equals(outboxStatus));
    }

    Optional<OrderProcessedOutboxMessage> findByOrderProcessedId(OrderId orderId) {
        return store.values().stream()
                .filter(m -> m.getAggregateId().equals(orderId.id()))
                .map(OrderProcessedOutboxMessage.class::cast)
                .findFirst();
    }

    void deleteAll() {
        store.clear();
    }
}