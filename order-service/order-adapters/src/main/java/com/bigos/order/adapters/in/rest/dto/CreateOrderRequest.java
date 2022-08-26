package com.bigos.order.adapters.in.rest.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID customerId,
        @NotNull UUID restaurantId,
        @NotNull BigDecimal price,
        @NotNull List<BasketItemCreateRequest> items,
        @NotNull OrderAddressCreateRequest address
) {
}
