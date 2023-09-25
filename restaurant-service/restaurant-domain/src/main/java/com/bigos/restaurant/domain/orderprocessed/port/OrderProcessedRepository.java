package com.bigos.restaurant.domain.orderprocessed.port;

import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;

public interface OrderProcessedRepository {

    OrderProcessed save(OrderProcessed orderProcessed);
}
