package com.bigos.restaurant.application.orderprocessed.outbox.repository;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;

import java.util.List;
import java.util.UUID;

public interface OrderProcessedOutboxRepository {

    <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage);

    <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus);

    <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClass, UUID sagaId, OutboxStatus outboxStatus);

    <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus);
}
