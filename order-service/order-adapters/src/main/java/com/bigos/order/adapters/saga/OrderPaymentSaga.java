package com.bigos.order.adapters.saga;

import com.bigos.common.adapters.saga.SagaStep;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.order.adapters.exception.OutboxMessageNotFoundException;
import com.bigos.order.adapters.outbox.dto.mapper.OrderOutboxMapper;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;
import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import com.bigos.order.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentStatusEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository outboxRepository;
    private final SagaStatusMapper sagaStatusMapper;
    private final OrderOutboxMapper orderOutboxMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderRepository orderRepository,
                            OrderOutboxRepository outboxRepository,
                            SagaStatusMapper sagaStatusMapper,
                            OrderOutboxMapper orderOutboxMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.sagaStatusMapper = sagaStatusMapper;
        this.orderOutboxMapper = orderOutboxMapper;
    }

    @Override
    @Transactional
    public void process(PaymentStatusEvent paymentStatusEvent) {
        Optional<OrderCreatedOutboxMessage> orderCreatedOutboxMessageOptional = outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(
                OrderCreatedOutboxMessage.class, UUID.fromString(paymentStatusEvent.sagaId()), SagaStatus.STARTED);

        if (orderCreatedOutboxMessageOptional.isEmpty()) {
            log.info("Outbox message is already processed. saga id: {}", paymentStatusEvent.sagaId());
            return;
        }

        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));
        OrderPaidEvent orderPaidEvent = orderDomainService.pay(order);
        orderRepository.save(order);

        SagaStatus sagaStatus = sagaStatusMapper.orderStatusToSagaStatus(order.getStatus());

        updateOrderCreatedOutboxMessage(orderCreatedOutboxMessageOptional.get(), sagaStatus);

        updateOrderPaidOutboxMessage(paymentStatusEvent, orderPaidEvent);
    }

    @Override
    @Transactional
    public void rollback(PaymentStatusEvent paymentStatusEvent) {

        Optional<OrderCreatedOutboxMessage> orderCreatedOutboxMessageOptional = outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(
                OrderCreatedOutboxMessage.class, UUID.fromString(paymentStatusEvent.sagaId()),
                sagaStatusMapper.paymentStatusToSagaStatus(paymentStatusEvent.paymentStatus()));

        if (orderCreatedOutboxMessageOptional.isEmpty()) {
            log.info("Outbox message is already processed. saga id: {}", paymentStatusEvent.sagaId());
            return;
        }

        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));
        orderDomainService.cancelOrder(order, paymentStatusEvent.failureMessages());
        orderRepository.save(order);

        SagaStatus sagaStatus = sagaStatusMapper.orderStatusToSagaStatus(order.getStatus());

        updateOrderCreatedOutboxMessage(orderCreatedOutboxMessageOptional.get(), sagaStatus);


        if (PaymentStatus.CANCELLED.equals(paymentStatusEvent.paymentStatus())) {
            updateOutboxCompensattingMessage(paymentStatusEvent, sagaStatus);
        }
    }

    private void updateOrderCreatedOutboxMessage(OrderCreatedOutboxMessage orderCreatedOutboxMessage, SagaStatus sagaStatus) {
        orderCreatedOutboxMessage.setProcessedDate(Instant.now());
        orderCreatedOutboxMessage.setSagaStatus(sagaStatus);
        outboxRepository.save(orderCreatedOutboxMessage);
    }

    private void updateOrderPaidOutboxMessage(PaymentStatusEvent paymentStatusEvent, OrderPaidEvent orderPaidEvent) {
        OrderPaidOutboxMessage orderPaidOutboxMessage =
                orderOutboxMapper.orderPaidEventToOrderPaidOutboxMessage(orderPaidEvent, UUID.fromString(paymentStatusEvent.sagaId()));
        outboxRepository.save(orderPaidOutboxMessage);
    }


    private void updateOutboxCompensattingMessage(PaymentStatusEvent paymentStatusEvent, SagaStatus sagaStatus) {
        List<OrderOutboxMessage> outboxCompensattingMessage = outboxRepository.findBySagaIdAndSagaStatus
                (UUID.fromString(paymentStatusEvent.sagaId()), SagaStatus.COMPENSATING);

        if (outboxCompensattingMessage.isEmpty()) {
            throw new OutboxMessageNotFoundException("Outbox messages not be found in Saga Status:" + SagaStatus.COMPENSATING.name());
        }

        outboxCompensattingMessage.forEach(message -> {
            outboxRepository.updateSagaStatusAndProcessDateById(sagaStatus, Instant.now(), message.getId());
        });
    }
}
