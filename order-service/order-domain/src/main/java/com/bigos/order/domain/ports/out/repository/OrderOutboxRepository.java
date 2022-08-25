package com.bigos.order.domain.ports.out.repository;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.order.domain.ports.dto.outbox.OrderOutboxMessage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {
    void save(OrderOutboxMessage outboxMessage);

    <G extends OrderOutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    List<OrderOutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus);

    <G extends OrderOutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClass, UUID sagaId, SagaStatus... sagaStatus);

    void updateSagaStatusAndProcessDateById(SagaStatus sagaStatus, Instant processedDate, UUID id);

}
