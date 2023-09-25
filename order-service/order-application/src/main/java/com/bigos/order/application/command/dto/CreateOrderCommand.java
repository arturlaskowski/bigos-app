package com.bigos.order.application.command.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateOrderCommand(
        UUID customerId,
        UUID restaurantId,
        BigDecimal price,
        List<BasketItemDto> items,
        OrderAddressDto address
) {
}
