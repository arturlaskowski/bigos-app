package com.bigos.payment.application.payment.outbox.publisher;


import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;

import java.util.function.Consumer;

public interface PaymentStatusMessagePublisher {

    void publish(PaymentOutboxMessage orderOutboxMessage, Consumer<PaymentOutboxMessage> outboxCallback);
}
