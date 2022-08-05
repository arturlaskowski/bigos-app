package com.bigos.order.domain.ports.out.repository;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.domain.model.Order;

public interface OrderRepository {

    Order save(Order order);

    Order getOrder(OrderId orderId);
}
