package com.bigos.restaurant.domain.orderprocessed.event;

import java.math.BigDecimal;

public record OrderItemDto(
        String productId,
        BigDecimal price,
        Integer quantity) {
}

