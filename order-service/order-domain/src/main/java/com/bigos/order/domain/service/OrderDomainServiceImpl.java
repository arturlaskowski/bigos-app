package com.bigos.order.domain.service;

import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.Order;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent initiate(Order order) {
        //TODO Move Restaurant Menu to shared kernel and get price and name product by id
        order.initialize();
        order.validate();
        log.info("Order with id {} was created", order.getId());
        return new OrderCreatedEvent(order, Instant.now());
    }

    @Override
    public OrderPaidEvent pay(Order order) {
        order.pay();
        log.info("Order with id: {} was paid", order.getId());
        return new OrderPaidEvent(order, Instant.now());
    }

    @Override
    public void approve(Order order) {
        order.approve();
        log.info("Order with id: {} was approved", order.getId());
    }

    @Override
    public OrderCancellingEvent startCancelling(Order order, String failureMessages) {
        order.startCancelling(failureMessages);
        log.info("Order with id: {} started canceling process", order.getId());
        return new OrderCancellingEvent(order, Instant.now());
    }

    @Override
    public void cancelOrder(Order order) {
        order.cancel();
        log.info("Order with id: {} was canceled", order.getId());
    }
}
