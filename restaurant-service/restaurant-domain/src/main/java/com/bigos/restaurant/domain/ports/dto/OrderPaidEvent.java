package com.bigos.restaurant.domain.ports.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaidEvent(
        String orderId,
        String restaurantId,
        BigDecimal price,
        List<OrderItemDto> orderItems,
        String status
) {
}
