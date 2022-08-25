package com.bigos.order.adapters.outbox.scheduler;

import com.bigos.common.adapters.outbox.scheduler.OutboxScheduler;
import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.order.domain.ports.dto.outbox.OrderCancellingOutboxMessage;
import com.bigos.order.domain.ports.out.message.OrderCancellingEventPublisher;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderCancellingOutboxScheduler implements OutboxScheduler {

    private final OrderCancellingEventPublisher orderCancellingEventPublisher;
    private final OrderOutboxRepository outboxRepository;

    public OrderCancellingOutboxScheduler(OrderCancellingEventPublisher orderCancellingEventPublisher, OrderOutboxRepository outboxRepository) {
        this.orderCancellingEventPublisher = orderCancellingEventPublisher;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        List<OrderCancellingOutboxMessage> orderCancellingOutboxMessages =
                outboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderCancellingOutboxMessage.class,
                        OutboxStatus.STARTED, SagaStatus.COMPENSATING);

        if (!orderCancellingOutboxMessages.isEmpty()) {
            orderCancellingOutboxMessages.forEach(outboxMessage ->
                    orderCancellingEventPublisher.publish(outboxMessage, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderCancellingOutboxMessage orderCancellingOutboxMessage) {
        outboxRepository.save(orderCancellingOutboxMessage);
    }
}
