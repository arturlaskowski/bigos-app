package com.bigos.order.domain.service;

import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.Order;

public interface OrderDomainService {

    OrderCreatedEvent create(Order order);

    OrderPaidEvent pay(Order order);

    void approve(Order order);

    OrderCancellingEvent startCancelling(Order order, String failureMessage);

    void cancelOrder(Order order, String failureMessage);
}
