package com.bigos.order.application.outbox.scheduler;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.applciaiton.outbox.scheduler.OutboxScheduler;
import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.order.application.outbox.publisher.OrderPaidEventPublisher;
import com.bigos.order.application.outbox.dto.OrderPaidOutboxMessage;
import com.bigos.order.application.outbox.repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderPaidOutboxScheduler implements OutboxScheduler {

    private final OrderPaidEventPublisher orderPaidEventPublisher;
    private final OrderOutboxRepository outboxRepository;

    public OrderPaidOutboxScheduler(OrderPaidEventPublisher orderPaidEventPublisher, OrderOutboxRepository outboxRepository) {
        this.orderPaidEventPublisher = orderPaidEventPublisher;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        List<OrderPaidOutboxMessage> orderPaidOutboxMessages =
                outboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderPaidOutboxMessage.class,
                        OutboxStatus.STARTED, SagaStatus.PROCESSING);

        if (!orderPaidOutboxMessages.isEmpty()) {
            orderPaidOutboxMessages.forEach(outboxMessage ->
                    orderPaidEventPublisher.publish(outboxMessage, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderPaidOutboxMessage orderPaidOutboxMessage) {
        outboxRepository.save(orderPaidOutboxMessage);
    }
}
