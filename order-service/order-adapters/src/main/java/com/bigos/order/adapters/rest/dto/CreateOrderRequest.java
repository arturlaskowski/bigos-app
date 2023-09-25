package com.bigos.order.adapters.rest.dto;

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

    public record BasketItemCreateRequest(
            @NotNull UUID productId,
            @NotNull Integer quantity,
            @NotNull BigDecimal price,
            @NotNull BigDecimal totalPrice) {
    }

    public record OrderAddressCreateRequest(
            @NotNull String street,
            @NotNull String postalCode,
            @NotNull String city,
            String houseNo
    ) {
    }
}
