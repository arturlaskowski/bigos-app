package com.bigos.order.domain.ports.dto.order.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BasketItemDto(
        UUID productId,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice) {
}
