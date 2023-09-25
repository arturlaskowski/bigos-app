package com.bigos.payment.entities.outbox;

import com.bigos.common.entities.outbox.OutboxEntityMapper;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;
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
