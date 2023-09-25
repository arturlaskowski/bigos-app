package com.bigos.order.application.query;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BasketItemProjection(
        Integer itemNumber,
        UUID productId,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice) {
}
