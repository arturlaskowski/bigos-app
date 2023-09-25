package com.bigos.common.applciaiton.outbox;

import com.bigos.common.applciaiton.outbox.model.AbstractOutboxMessage;

import java.util.function.Consumer;

public interface OutboxPublisher<T extends AbstractOutboxMessage> {

    void publish(T outboxMessage, Consumer<T> outboxCallback);
}
