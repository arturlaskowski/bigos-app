package com.bigos.payment.adapters.payment.repository;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.application.payment.exception.PaymentOutboxNotFoundException;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;
import com.bigos.payment.application.payment.outbox.repository.PaymentOutboxRepository;
import com.bigos.payment.entities.outbox.PaymentOutboxEntity;
import com.bigos.payment.entities.outbox.PaymentOutboxEntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlPaymentOutboxRepository implements PaymentOutboxRepository {

    private final PaymentOutboxRepositoryJpa outboxRepositoryJpa;
    private final PaymentOutboxEntityMapper mapper;

    public SqlPaymentOutboxRepository(PaymentOutboxRepositoryJpa outboxRepositoryJpa, PaymentOutboxEntityMapper mapper) {
        this.outboxRepositoryJpa = outboxRepositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public void save(PaymentOutboxMessage outboxMessage) {
        outboxRepositoryJpa.save(mapper.paymentOutboxMessageToPaymentOutboxEntity(outboxMessage));
    }

    @Override
    public <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.findByMessageTypeAndOutboxStatus(outboxMessageClass.getSimpleName(), outboxStatus)
                .orElseThrow(() -> new PaymentOutboxNotFoundException(outboxMessageClass.getSimpleName() + " object cannot be found"))
                .stream()
                .map(entity -> mapper.paymentOutboxEntityToPaymentOutboxMessage(entity, outboxMessageClass))
                .toList();
    }

    @Override
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        outboxRepositoryJpa.deleteByOutboxStatus(outboxStatus);
    }

    public <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus
            (Class<G> outboxMessageClass, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus) {
        return outboxRepositoryJpa.existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus
                (outboxMessageClass.getSimpleName(), sagaId, paymentStatus, outboxStatus);
    }
}

@Repository
interface PaymentOutboxRepositoryJpa extends JpaRepository<PaymentOutboxEntity, UUID> {

    Optional<List<PaymentOutboxEntity>> findByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus
            (String type, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus);

    void deleteByOutboxStatus(OutboxStatus outboxStatus);

}
