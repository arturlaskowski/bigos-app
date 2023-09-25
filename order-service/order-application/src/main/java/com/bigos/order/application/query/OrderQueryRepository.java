package com.bigos.order.application.query;

import java.util.UUID;

public interface OrderQueryRepository {

    OrderProjection getOrderProjection(UUID orderId);
}
