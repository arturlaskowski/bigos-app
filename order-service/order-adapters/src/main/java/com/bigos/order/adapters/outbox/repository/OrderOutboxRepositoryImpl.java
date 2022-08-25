package com.bigos.order.adapters.outbox.repository;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.order.adapters.outbox.model.entity.OrderOutboxEntity;
import com.bigos.order.adapters.outbox.model.mapper.OrderOutboxEntityMapper;
import com.bigos.order.domain.ports.dto.outbox.OrderOutboxMessage;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxRepositoryJpa outboxRepositoryJpa;
    private final OrderOutboxEntityMapper mapper;

    public OrderOutboxRepositoryImpl(OrderOutboxRepositoryJpa outboxRepositoryJpa, OrderOutboxEntityMapper mapper) {
        this.outboxRepositoryJpa = outboxRepositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public void save(OrderOutboxMessage outboxMessage) {
        outboxRepositoryJpa.save(
                mapper.orderOutboxMessageToOrderOutboxEntity(outboxMessage));
    }

    @Override
    public <G extends OrderOutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return outboxRepositoryJpa.findByMessageTypeAndOutboxStatusAndSagaStatusIn(outboxMessageClass.getSimpleName(), outboxStatus, Arrays.asList(sagaStatus))
                .stream()
                .map(entity -> mapper.orderOutboxEntityToOrderOutboxMessage(entity, outboxMessageClass))
                .toList();
    }

    @Override
    public void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        outboxRepositoryJpa.deleteByOutboxStatusAndSagaStatusIn(outboxStatus, Arrays.asList(sagaStatus));
    }

    @Override
    public List<OrderOutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return outboxRepositoryJpa.findBySagaIdAndSagaStatusIn(sagaId, Arrays.asList(sagaStatus))
                .stream()
                .map(entity -> mapper.orderOutboxEntityToOrderOutboxMessage(entity, OrderOutboxMessage.class))
                .toList();
    }

    @Override
    public <G extends OrderOutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClass, UUID sagaId, SagaStatus... sagaStatus) {
        return outboxRepositoryJpa.findByMessageTypeAndSagaIdAndSagaStatusIn(outboxMessageClass.getSimpleName(), sagaId, Arrays.asList(sagaStatus))
                .map(outboxEntity -> mapper.orderOutboxEntityToOrderOutboxMessage(outboxEntity, outboxMessageClass));
    }

    @Override
    public void updateSagaStatusAndProcessDateById(SagaStatus sagaStatus, Instant processedDate, UUID id) {
        outboxRepositoryJpa.updateSagaStatusAndProcessDateById(sagaStatus, processedDate, id);
    }
}

@Repository
interface OrderOutboxRepositoryJpa extends JpaRepository<OrderOutboxEntity, UUID> {

    List<OrderOutboxEntity> findByMessageTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatuses);

    Optional<OrderOutboxEntity> findByMessageTypeAndSagaIdAndSagaStatusIn(String type, UUID sagaId, List<SagaStatus> sagaStatus);

    List<OrderOutboxEntity> findBySagaIdAndSagaStatusIn(UUID sagaId, List<SagaStatus> sagaStatus);

    void deleteByOutboxStatusAndSagaStatusIn(OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);

    @Modifying
    @Query("update OrderOutboxEntity o set o.sagaStatus = ?1, o.processedDate = ?2 where o.id = ?3")
    void updateSagaStatusAndProcessDateById(SagaStatus sagaStatus, Instant processedDate, UUID id);
}
