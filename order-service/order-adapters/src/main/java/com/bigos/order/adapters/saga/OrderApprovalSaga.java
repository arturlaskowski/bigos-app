package com.bigos.order.adapters.saga;

import com.bigos.common.adapters.saga.SagaStep;
import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.adapters.exception.OutboxMessageNotFoundException;
import com.bigos.order.adapters.outbox.dto.mapper.OrderOutboxMapper;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.outbox.OrderCancellingOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;
import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import com.bigos.order.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository outboxRepository;
    private final SagaStatusMapper sagaStatusMapper;
    private final OrderOutboxMapper orderOutboxMapper;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             OrderOutboxRepository outboxRepository,
                             SagaStatusMapper sagaStatusMapper, OrderOutboxMapper orderOutboxMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.sagaStatusMapper = sagaStatusMapper;
        this.orderOutboxMapper = orderOutboxMapper;
    }

    @Override
    @Transactional
    public void process(RestaurantApprovalEvent restaurantApprovalEvent) {
        Optional<OrderPaidOutboxMessage> orderPaidOutboxMessageOptional =
                outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderPaidOutboxMessage.class,
                        UUID.fromString(restaurantApprovalEvent.sagaId()), SagaStatus.PROCESSING);


        if (orderPaidOutboxMessageOptional.isEmpty()) {
            log.info("Outbox message is already processed. saga id: {}", restaurantApprovalEvent.sagaId());
            return;
        }

        Order order = approveOrder(restaurantApprovalEvent);

        SagaStatus sagaStatus = sagaStatusMapper.orderStatusToSagaStatus(order.getStatus());

        updateOrderPaidOutboxMessage(orderPaidOutboxMessageOptional.get(), sagaStatus);

        updateOrderCreatedOutboxMessage(restaurantApprovalEvent, sagaStatus);
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalEvent restaurantApprovalEvent) {
        Optional<OrderPaidOutboxMessage> orderPaidOutboxMessageOptional =
                outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderPaidOutboxMessage.class,
                        UUID.fromString(restaurantApprovalEvent.sagaId()), SagaStatus.PROCESSING);

        if (orderPaidOutboxMessageOptional.isEmpty()) {
            log.info("Outbox message is already roll backed. saga id: {}", restaurantApprovalEvent.sagaId());
            return;
        }

        OrderCancellingEvent orderCancellingEvent = initCancellingOrder(restaurantApprovalEvent);

        SagaStatus sagaStatus = sagaStatusMapper.orderStatusToSagaStatus(orderCancellingEvent.getOrder().getStatus());

        updateOrderPaidOutboxMessage(orderPaidOutboxMessageOptional.get(), sagaStatus);

        updateOrderCancellingOutboxMessage(restaurantApprovalEvent, orderCancellingEvent);
    }

    private Order approveOrder(RestaurantApprovalEvent restaurantApprovalEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(restaurantApprovalEvent.orderId())));
        orderDomainService.approve(order);
        orderRepository.save(order);
        return order;
    }

    private void updateOrderPaidOutboxMessage(OrderPaidOutboxMessage orderPaidOutboxMessage, SagaStatus sagaStatus) {
        orderPaidOutboxMessage.setProcessedDate(Instant.now());
        orderPaidOutboxMessage.setSagaStatus(sagaStatus);
        outboxRepository.save(orderPaidOutboxMessage);
    }

    private void updateOrderCreatedOutboxMessage(RestaurantApprovalEvent restaurantApprovalEvent, SagaStatus sagaStatus) {
        Optional<OrderCreatedOutboxMessage> orderCreatedOutboxMessageOptional = outboxRepository
                .findByMessageTypeAndSagaIdAndSagaStatus(OrderCreatedOutboxMessage.class,
                        UUID.fromString(restaurantApprovalEvent.sagaId()), SagaStatus.PROCESSING);

        if (orderCreatedOutboxMessageOptional.isEmpty()) {
            throw new OutboxMessageNotFoundException("OrderCreatedOutboxMessage cannot be found in Saga Status:" + SagaStatus.PROCESSING.name());
        }
        OrderCreatedOutboxMessage orderCreatedOutboxMessage = orderCreatedOutboxMessageOptional.get();
        orderCreatedOutboxMessage.setProcessedDate(Instant.now());
        orderCreatedOutboxMessage.setSagaStatus(sagaStatus);

        outboxRepository.save(orderCreatedOutboxMessage);
    }

    private void updateOrderCancellingOutboxMessage(RestaurantApprovalEvent restaurantApprovalEvent, OrderCancellingEvent orderCancellingEvent) {
        OrderCancellingOutboxMessage orderCancellingOutboxMessage = orderOutboxMapper.orderCancellingEventToOrderCancellingOutboxMessage
                (orderCancellingEvent, UUID.fromString(restaurantApprovalEvent.sagaId()));

        outboxRepository.save(orderCancellingOutboxMessage);
    }

    private OrderCancellingEvent initCancellingOrder(RestaurantApprovalEvent restaurantApprovalEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(restaurantApprovalEvent.orderId())));
        OrderCancellingEvent orderCancellingEvent = orderDomainService.startCancelling(order, restaurantApprovalEvent.failureMessages());
        orderRepository.save(order);
        return orderCancellingEvent;
    }
}
