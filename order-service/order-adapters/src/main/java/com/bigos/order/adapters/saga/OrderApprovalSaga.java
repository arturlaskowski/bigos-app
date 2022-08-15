package com.bigos.order.adapters.saga;

import com.bigos.common.adapters.saga.SagaStep;
import com.bigos.common.domain.event.EmptyEvent;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;
import com.bigos.order.domain.ports.out.message.OrderCancellingEventPublisher;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import com.bigos.order.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalEvent, EmptyEvent, OrderCancellingEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderCancellingEventPublisher orderCancellingEventPublisher;

    public OrderApprovalSaga(OrderDomainService orderDomainService, OrderRepository orderRepository, OrderCancellingEventPublisher orderCancellingEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderCancellingEventPublisher = orderCancellingEventPublisher;
    }

    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalEvent restaurantApprovalEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(restaurantApprovalEvent.orderId())));

        orderDomainService.approve(order);

        orderRepository.save(order);

        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancellingEvent rollback(RestaurantApprovalEvent restaurantApprovalEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(restaurantApprovalEvent.orderId())));

        OrderCancellingEvent orderCancellingEvent = orderDomainService.startCancelling(order, restaurantApprovalEvent.failureMessages());

        orderRepository.save(order);

        orderCancellingEventPublisher.publish(orderCancellingEvent);

        return orderCancellingEvent;
    }
}
