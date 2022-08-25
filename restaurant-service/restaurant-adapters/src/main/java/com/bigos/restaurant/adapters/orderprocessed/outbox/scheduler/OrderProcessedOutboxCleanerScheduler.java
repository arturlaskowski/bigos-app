package com.bigos.restaurant.adapters.orderprocessed.outbox.scheduler;

import com.bigos.common.adapters.outbox.scheduler.OutboxScheduler;
import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderProcessedOutboxCleanerScheduler implements OutboxScheduler {

    private final OrderProcessedOutboxRepository outboxRepository;

    public OrderProcessedOutboxCleanerScheduler(OrderProcessedOutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    @Scheduled(cron = "@midnight")
    @Override
    public void processOutboxMessage() {
        log.info("Started deleting order processed outbox messages");

        outboxRepository.deleteByTypeAndOutboxStatus(OrderProcessedOutboxMessage.class, OutboxStatus.COMPLETED);

        log.info("Finished deleting order processed outbox messages");
    }
}