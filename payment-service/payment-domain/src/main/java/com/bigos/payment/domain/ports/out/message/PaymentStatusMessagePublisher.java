package com.bigos.payment.domain.ports.out.message;

import com.bigos.payment.domain.ports.dto.outbox.PaymentOutboxMessage;

import java.util.function.Consumer;

public interface PaymentStatusMessagePublisher {

    void publish(PaymentOutboxMessage orderOutboxMessage, Consumer<PaymentOutboxMessage> outboxCallback);
}
