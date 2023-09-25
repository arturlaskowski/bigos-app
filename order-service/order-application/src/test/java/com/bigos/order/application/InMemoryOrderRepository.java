package com.bigos.order.application;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.port.OrderRepository;

import java.util.HashMap;
import java.util.Map;

class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new HashMap<>();

    @Override
    public Order save(final Order order) {
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Order getOrder(final OrderId orderId) {
        return store.get(orderId);
    }

    void deleteAll() {
        store.clear();
    }
}
