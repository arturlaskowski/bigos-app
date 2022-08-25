package com.bigos.restaurant.adapters.orderprocessed.outbox.scheduler;

import com.bigos.common.adapters.outbox.scheduler.OutboxScheduler;
import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;
import com.bigos.restaurant.domain.ports.out.message.OrderApprovalMessagePublisher;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderProcessedOutboxScheduler implements OutboxScheduler {

    private final OrderProcessedOutboxRepository outboxRepository;
    private final OrderApprovalMessagePublisher orderApprovalMessagePublisher;

    public OrderProcessedOutboxScheduler(OrderProcessedOutboxRepository outboxRepository,
                                         OrderApprovalMessagePublisher orderApprovalMessagePublisher
    ) {
        this.outboxRepository = outboxRepository;
        this.orderApprovalMessagePublisher = orderApprovalMessagePublisher;
    }


    @Transactional
    @Scheduled(fixedRateString = "${restaurant-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}")
    @Override
    public void processOutboxMessage() {
        List<OrderProcessedOutboxMessage> outboxMessages =
                outboxRepository.findByTypeAndOutboxStatus(OrderProcessedOutboxMessage.class, OutboxStatus.STARTED);

        outboxMessages.forEach(orderOutboxMessage ->
                orderApprovalMessagePublisher.publish(orderOutboxMessage, this::saveOutboxMessage));
    }

    public void saveOutboxMessage(OrderProcessedOutboxMessage outboxMessage) {
        outboxRepository.save(outboxMessage);
    }
}