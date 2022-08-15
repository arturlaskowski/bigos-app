package com.bigos.order.domain.ports.dto.order.create;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        @NotNull UUID customerId,
        @NotNull UUID restaurantId,
        @NotNull BigDecimal price,
        @NotNull List<BasketItemCreateDto> items,
        @NotNull OrderAddressCreateDto address
) {
}
