package com.bigos.payment.application.payment.outbox.scheduler;

import com.bigos.common.applciaiton.outbox.scheduler.OutboxScheduler;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;
import com.bigos.payment.application.payment.outbox.publisher.PaymentStatusMessagePublisher;
import com.bigos.payment.application.payment.outbox.repository.PaymentOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bigos.common.applciaiton.outbox.model.OutboxStatus.STARTED;

@Slf4j
@Component
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentStatusMessagePublisher paymentStatusMessagePublisher;
    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentOutboxScheduler(PaymentStatusMessagePublisher paymentStatusMessagePublisher, PaymentOutboxRepository paymentOutboxRepository) {
        this.paymentStatusMessagePublisher = paymentStatusMessagePublisher;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        List<PaymentOutboxMessage> paymentOutboxMessages
                = paymentOutboxRepository.findByMessageTypeAndOutboxStatus(PaymentOutboxMessage.class, STARTED);

        if (!paymentOutboxMessages.isEmpty()) {
            paymentOutboxMessages.forEach(paymentOutboxMessage ->
                    paymentStatusMessagePublisher.publish(paymentOutboxMessage, this::saveOutboxMessage));
        }
    }

    public void saveOutboxMessage(PaymentOutboxMessage paymentOutboxMessage) {
        paymentOutboxRepository.save(paymentOutboxMessage);
    }

}
