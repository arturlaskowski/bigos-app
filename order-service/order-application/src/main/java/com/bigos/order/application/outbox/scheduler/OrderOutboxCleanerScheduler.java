
package com.bigos.order.application.outbox.scheduler;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.applciaiton.outbox.scheduler.OutboxScheduler;
import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.order.application.outbox.repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private final OrderOutboxRepository outboxRepository;

    public OrderOutboxCleanerScheduler(OrderOutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {

        log.info("Started deleting order outbox messages");

        outboxRepository.deleteByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);

        log.info("Finished deleting order outbox messages");
    }
}

