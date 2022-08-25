package com.bigos.common.adapters.outbox.scheduler;

public interface OutboxScheduler {
    void processOutboxMessage();
}
