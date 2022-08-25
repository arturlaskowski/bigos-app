package com.bigos.payment.adapters.payment.outbox.scheduler;

import com.bigos.common.adapters.outbox.scheduler.OutboxScheduler;
import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.payment.domain.ports.out.repository.PaymentOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentOutboxCleanerScheduler(PaymentOutboxRepository paymentOutboxRepository) {
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {

        log.info("Started deleting order outbox messages");

        paymentOutboxRepository.deleteByOutboxStatus(OutboxStatus.COMPLETED);

        log.info("Finished deleting order outbox messages");
    }
}
