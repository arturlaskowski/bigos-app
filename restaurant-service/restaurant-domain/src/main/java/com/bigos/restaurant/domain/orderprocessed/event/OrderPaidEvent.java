package com.bigos.restaurant.domain.orderprocessed.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderPaidEvent(
        String orderId,
        String sageId,
        String restaurantId,
        BigDecimal price,
        List<OrderItemDto> orderItems,
        String status
) {
}
