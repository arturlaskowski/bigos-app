package com.bigos.order.domain;

import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.core.Order;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class OrderDomainService {

    public OrderCreatedEvent create(Order order) {
        //TODO Create a restaurant menu as a shared kernel and get price and name product by id
        order.initialize();
        order.validatePrice();
        log.info("Order with id {} was created", order.getId());
        return new OrderCreatedEvent(order, Instant.now());
    }

    public OrderPaidEvent pay(Order order) {
        order.pay();
        log.info("Order with id: {} was paid", order.getId());
        return new OrderPaidEvent(order, Instant.now());
    }

    public void approve(Order order) {
        order.approve();
        log.info("Order with id: {} was approved", order.getId());
    }

    public OrderCancellingEvent startCancelling(Order order, String failureMessage) {
        order.startCancelling(failureMessage);
        log.info("Order with id: {} started canceling process", order.getId());
        return new OrderCancellingEvent(order, Instant.now());
    }

    public void cancelOrder(Order order, String failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} was canceled", order.getId());
    }
}
