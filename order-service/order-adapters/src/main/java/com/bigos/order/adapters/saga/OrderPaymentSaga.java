package com.bigos.order.adapters.saga;

import com.bigos.common.adapters.saga.SagaStep;
import com.bigos.common.domain.event.EmptyEvent;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;
import com.bigos.order.domain.ports.out.message.OrderPaidEventPublisher;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import com.bigos.order.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentStatusEvent, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderPaidEventPublisher orderPaidEventPublisher;

    public OrderPaymentSaga(OrderDomainService orderDomainService, OrderRepository orderRepository, OrderPaidEventPublisher orderPaidEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderPaidEventPublisher = orderPaidEventPublisher;
    }

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentStatusEvent paymentStatusEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));

        OrderPaidEvent orderPaidEvent = orderDomainService.pay(order);

        orderRepository.save(order);

        orderPaidEventPublisher.publish(orderPaidEvent);

        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentStatusEvent paymentStatusEvent) {
        Order order = orderRepository.getOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));

        orderDomainService.cancelOrder(order, paymentStatusEvent.failureMessages());

        orderRepository.save(order);

        return EmptyEvent.INSTANCE;
    }
}
