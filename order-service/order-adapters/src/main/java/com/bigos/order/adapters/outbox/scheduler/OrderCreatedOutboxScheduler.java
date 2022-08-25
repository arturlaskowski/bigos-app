package com.bigos.order.adapters.outbox.scheduler;

import com.bigos.common.adapters.outbox.scheduler.OutboxScheduler;
import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.out.message.OrderCreatedEventPublisher;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderCreatedOutboxScheduler implements OutboxScheduler {

    private final OrderCreatedEventPublisher orderCreatedEventPublisher;
    private final OrderOutboxRepository outboxRepository;

    public OrderCreatedOutboxScheduler(OrderCreatedEventPublisher orderCreatedEventPublisher, OrderOutboxRepository outboxRepository) {
        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        List<OrderCreatedOutboxMessage> orderCreatedOutboxMessages =
                outboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderCreatedOutboxMessage.class,
                        OutboxStatus.STARTED, SagaStatus.STARTED);

        if (!orderCreatedOutboxMessages.isEmpty()) {
            orderCreatedOutboxMessages.forEach(outboxMessage ->
                    orderCreatedEventPublisher.publish(outboxMessage, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderCreatedOutboxMessage orderCreatedOutboxMessage) {
        outboxRepository.save(orderCreatedOutboxMessage);
    }
}
