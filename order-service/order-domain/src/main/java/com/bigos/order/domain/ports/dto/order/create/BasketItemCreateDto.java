package com.bigos.order.domain.ports.dto.order.create;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record BasketItemCreateDto(
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal price,
        @NotNull BigDecimal totalPrice) {
}
