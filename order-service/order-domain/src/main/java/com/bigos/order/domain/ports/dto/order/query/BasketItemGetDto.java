package com.bigos.order.domain.ports.dto.order.query;

import java.math.BigDecimal;
import java.util.UUID;

public record BasketItemGetDto(
        Integer itemNumber,
        UUID productId,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice) {
}
