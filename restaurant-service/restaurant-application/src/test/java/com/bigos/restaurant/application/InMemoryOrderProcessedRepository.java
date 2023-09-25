package com.bigos.restaurant.application;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.orderprocessed.port.OrderProcessedRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryOrderProcessedRepository implements OrderProcessedRepository {

    private final Map<UUID, OrderProcessed> store = new HashMap<>();

    @Override
    public OrderProcessed save(OrderProcessed orderProcessed) {
        store.put(orderProcessed.getId().id(), orderProcessed);
        return orderProcessed;
    }

    Optional<OrderProcessed> findById(OrderId orderId) {
        return Optional.ofNullable(store.get(orderId.id()));
    }

    void deleteAll() {
        store.clear();
    }
}
