package com.bigos.payment.application.payment;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;
import com.bigos.payment.application.payment.outbox.repository.PaymentOutboxRepository;
import com.bigos.payment.domain.core.PaymentId;

import java.util.*;
import java.util.stream.Collectors;

class InMemoryPaymentOutboxRepository implements PaymentOutboxRepository {

    private final Map<UUID, PaymentOutboxMessage> store = new HashMap<>();

    @Override
    public <G extends PaymentOutboxMessage> void save(G outboxMessage) {
        store.put(outboxMessage.getId(), outboxMessage);
    }

    @Override
    public <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus) {
        return store.values().stream()
                .filter(outboxMessageClass::isInstance)
                .map(outboxMessageClass::cast)
                .filter(outboxMessage -> outboxMessage.getOutboxStatus().equals(outboxStatus))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        store.values().removeIf(outboxMessage -> outboxMessage.getOutboxStatus().equals(outboxStatus));
    }

    @Override
    public <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            Class<G> outboxMessageClass, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus) {
        return store.values().stream()
                .anyMatch(outboxMessage -> outboxMessageClass.isInstance(outboxMessage)
                        && Objects.equals(outboxMessage.getSagaId(), sagaId)
                        && Objects.equals(outboxMessage.getPaymentStatus(), paymentStatus)
                        && Objects.equals(outboxMessage.getOutboxStatus(), outboxStatus));
    }

    Optional<PaymentOutboxMessage> findLastByPaymentId(PaymentId paymentId) {
        return store.values().stream()
                .filter(m -> m.getAggregateId().equals(paymentId.id()))
                .max(Comparator.comparing(PaymentOutboxMessage::getCreatedDate));
    }

    List<PaymentOutboxMessage> getAll(){
        return store.values().stream().toList();
    }

    void deleteAll() {
        store.clear();
    }
}
