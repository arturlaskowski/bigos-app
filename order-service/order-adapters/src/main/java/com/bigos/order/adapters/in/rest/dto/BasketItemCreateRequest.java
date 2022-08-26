package com.bigos.order.adapters.in.rest.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record BasketItemCreateRequest(
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal price,
        @NotNull BigDecimal totalPrice) {
}
