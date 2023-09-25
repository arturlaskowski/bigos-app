package com.bigos.restaurant.adapters.orderprocessed.repository;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;
import com.bigos.restaurant.application.orderprocessed.outbox.exception.OrderProcessedOutboxNotFoundException;
import com.bigos.restaurant.application.orderprocessed.outbox.repository.OrderProcessedOutboxRepository;
import com.bigos.restaurant.entities.orderprocessed.outbox.OrderProcessedOutboxEntity;
import com.bigos.restaurant.entities.orderprocessed.outbox.OrderProcessedOutboxEntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlOrderProcessedOutboxRepository implements OrderProcessedOutboxRepository {

    private final OrderProcessedOutboxRepositoryJpa outboxRepositoryJpa;
    private final OrderProcessedOutboxEntityMapper mapper;

    public SqlOrderProcessedOutboxRepository(OrderProcessedOutboxRepositoryJpa outboxRepositoryJpa, OrderProcessedOutboxEntityMapper mapper) {
        this.outboxRepositoryJpa = outboxRepositoryJpa;
        this.mapper = mapper;
    }

    public <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage) {
        outboxRepositoryJpa.save(mapper.orderProcessedOutboxMessageToOrderProcessedOutboxEntity(orderOutboxMessage));
    }

    public <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.findByMessageTypeAndOutboxStatus(outboxMessageClass.getSimpleName(), outboxStatus)
                .orElseThrow(() -> new OrderProcessedOutboxNotFoundException("OrderProcessed outbox not found. MessageType: " + outboxMessageClass.getSimpleName()))
                .stream()
                .map(entity -> mapper.orderProcessedOutboxEntityToOrderProcessedOutboxMessage(entity, outboxMessageClass))
                .toList();
    }

    public <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClass, UUID sagaId, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.existsByMessageTypeAndSagaIdAndOutboxStatus(outboxMessageClass.getSimpleName(), sagaId, outboxStatus);
    }

    public <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        outboxRepositoryJpa.deleteByMessageTypeAndOutboxStatus(outboxMessageClass.getSimpleName(), outboxStatus);
    }
}

@Repository
interface OrderProcessedOutboxRepositoryJpa extends JpaRepository<OrderProcessedOutboxEntity, UUID> {

    Optional<List<OrderProcessedOutboxEntity>> findByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    boolean existsByMessageTypeAndSagaIdAndOutboxStatus(String type, UUID sagaID, OutboxStatus outboxStatus);

    void deleteByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
