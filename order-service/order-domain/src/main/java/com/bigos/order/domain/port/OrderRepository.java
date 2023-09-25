package com.bigos.order.domain.port;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.domain.core.Order;

public interface OrderRepository {

    Order save(Order order);

    Order getOrder(OrderId orderId);
}
