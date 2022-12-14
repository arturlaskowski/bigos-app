package com.bigos.infrastructure.kafka.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record BasketItemMessageDto(
        Integer itemNumber,
        String productId,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice
) implements Serializable {
}
