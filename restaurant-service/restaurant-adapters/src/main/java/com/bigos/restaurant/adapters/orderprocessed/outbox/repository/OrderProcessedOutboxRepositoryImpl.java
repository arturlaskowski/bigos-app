package com.bigos.restaurant.adapters.orderprocessed.outbox.repository;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.restaurant.adapters.orderprocessed.outbox.exception.OrderProcessedOutboxNotFoundException;
import com.bigos.restaurant.adapters.orderprocessed.outbox.model.entity.OrderProcessedOutboxEntity;
import com.bigos.restaurant.adapters.orderprocessed.outbox.model.mapper.OrderProcessedOutboxEntityMapper;
import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedOutboxRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderProcessedOutboxRepositoryImpl implements OrderProcessedOutboxRepository {

    private final OrderProcessedOutboxRepositoryJpa outboxRepositoryJpa;
    private final OrderProcessedOutboxEntityMapper mapper;

    public OrderProcessedOutboxRepositoryImpl(OrderProcessedOutboxRepositoryJpa outboxRepositoryJpa, OrderProcessedOutboxEntityMapper mapper) {
        this.outboxRepositoryJpa = outboxRepositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage) {
        outboxRepositoryJpa.save(mapper.orderProcessedOutboxMessageToOrderProcessedOutboxEntity(orderOutboxMessage));
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.findByMessageTypeAndOutboxStatus(outboxMessageClass.getSimpleName(), outboxStatus)
                .orElseThrow(() -> new OrderProcessedOutboxNotFoundException("OrderProcessed outbox not found. MessageType: " + outboxMessageClass.getSimpleName()))
                .stream()
                .map(entity -> mapper.orderProcessedOutboxEntityToOrderProcessedOutboxMessage(entity, outboxMessageClass))
                .toList();
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClass, UUID sagaId, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.existsByMessageTypeAndSagaIdAndOutboxStatus(outboxMessageClass.getSimpleName(), sagaId, outboxStatus);
    }

    @Override
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
