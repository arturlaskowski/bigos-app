package com.bigos.payment.application.payment.outbox.repository;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;

import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository {
    <G extends PaymentOutboxMessage> void save(G outboxMessage);

    <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClass, OutboxStatus outboxStatus);

    void deleteByOutboxStatus(OutboxStatus outboxStatus);

    <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            Class<G> outboxMessageClass, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus);

}
