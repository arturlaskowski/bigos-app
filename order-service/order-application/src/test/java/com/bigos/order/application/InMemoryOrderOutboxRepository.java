package com.bigos.order.application;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.application.outbox.OrderOutboxMessage;
import com.bigos.order.application.outbox.dto.OrderCreatedOutboxMessage;
import com.bigos.order.application.outbox.repository.OrderOutboxRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderOutboxRepository implements OrderOutboxRepository {

    private final Map<UUID, OrderOutboxMessage> store = new HashMap<>();

    @Override
    public void save(OrderOutboxMessage outboxMessage) {
        store.put(outboxMessage.getId(), outboxMessage);
    }

    @Override
    public <G extends OrderOutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        List<SagaStatus> sagaStatusList = Arrays.asList(sagaStatus);
        return store.values().stream()
                .filter(outboxMessageClass::isInstance)
                .filter(m -> m.getOutboxStatus().equals(outboxStatus))
                .filter(m -> sagaStatusList.contains(m.getSagaStatus()))
                .map(outboxMessageClass::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        List<SagaStatus> sagaStatusList = Arrays.asList(sagaStatus);
        store.entrySet().removeIf(entry -> entry.getValue().getOutboxStatus().equals(outboxStatus) && sagaStatusList.contains(entry.getValue().getSagaStatus()));
    }

    @Override
    public List<OrderOutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        List<SagaStatus> sagaStatusList = Arrays.asList(sagaStatus);
        return store.values().stream()
                .filter(m -> m.getSagaId().equals(sagaId))
                .filter(m -> sagaStatusList.contains(m.getSagaStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public <G extends OrderOutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClass, UUID sagaId, SagaStatus... sagaStatus) {
        List<SagaStatus> sagaStatusList = Arrays.asList(sagaStatus);
        return store.values().stream()
                .filter(outboxMessageClass::isInstance)
                .filter(m -> m.getSagaId().equals(sagaId))
                .filter(m -> sagaStatusList.contains(m.getSagaStatus()))
                .map(outboxMessageClass::cast)
                .findFirst();
    }

    @Override
    public void updateSagaStatusAndProcessDateById(SagaStatus sagaStatus, Instant processedDate, UUID id) {
        OrderOutboxMessage message = store.get(id);
        if (message != null) {
            message.setSagaStatus(sagaStatus);
            message.setProcessedDate(processedDate);
        }
    }

    public Optional<OrderCreatedOutboxMessage> findByOrderId(OrderId orderId) {
        return store.values().stream()
                .filter(OrderCreatedOutboxMessage.class::isInstance)
                .filter(m -> m.getAggregateId().equals(orderId.id()))
                .map(OrderCreatedOutboxMessage.class::cast)
                .findFirst();
    }

    void deleteAll() {
        store.clear();
    }
}