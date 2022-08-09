package com.bigos.restaurant.domain.ports.out.repository;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.restaurant.domain.model.OrderProcessed;

import java.util.Optional;

public interface OrderProcessedRepository {

    OrderProcessed save(OrderProcessed orderProcessed);

    //for tests
    Optional<OrderProcessed> findById(OrderId orderId);
}
