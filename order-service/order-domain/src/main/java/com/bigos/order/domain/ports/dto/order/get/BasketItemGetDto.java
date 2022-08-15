package com.bigos.order.domain.ports.dto.order.get;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record BasketItemGetDto(
        @NotNull Integer itemNumber,
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal price,
        @NotNull BigDecimal totalPrice) {
}
