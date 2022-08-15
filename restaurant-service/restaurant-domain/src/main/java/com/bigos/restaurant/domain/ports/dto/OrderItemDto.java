package com.bigos.restaurant.domain.ports.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        String productId,
        BigDecimal price,
        Integer quantity) {
}

