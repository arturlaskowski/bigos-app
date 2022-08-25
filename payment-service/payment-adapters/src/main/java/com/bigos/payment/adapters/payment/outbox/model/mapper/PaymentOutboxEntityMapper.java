package com.bigos.payment.adapters.payment.outbox.model.mapper;

import com.bigos.common.adapters.outbox.model.entity.mapper.OutboxEntityMapper;
import com.bigos.payment.adapters.payment.outbox.model.entity.PaymentOutboxEntity;
import com.bigos.payment.domain.ports.dto.outbox.PaymentOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class PaymentOutboxEntityMapper extends OutboxEntityMapper<PaymentOutboxEntity> {

    public <G extends PaymentOutboxMessage> G paymentOutboxEntityToPaymentOutboxMessage(PaymentOutboxEntity outboxEntity, Class<G> outboxMessageClass) {
        G outboxMessage = super.outboxEntityToOutboxMessage(outboxEntity, outboxMessageClass);
        outboxMessage.setPaymentStatus(outboxEntity.getPaymentStatus());
        return outboxMessage;
    }

    public PaymentOutboxEntity paymentOutboxMessageToPaymentOutboxEntity(PaymentOutboxMessage outboxMessage) {
        PaymentOutboxEntity paymentOutboxEntity = super.outboxMessageToOutboxEntity(outboxMessage, PaymentOutboxEntity.class);
        paymentOutboxEntity.setMessageType(PaymentOutboxMessage.class.getSimpleName());
        paymentOutboxEntity.setPaymentStatus(outboxMessage.getPaymentStatus());
        return paymentOutboxEntity;
    }
}
