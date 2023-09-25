package com.bigos.order.application.query;

import com.bigos.common.domain.vo.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderProjection(
        UUID id,
        UUID customerId,
        UUID restaurantId,
        BigDecimal price,
        OrderStatus status,
        Instant creationDate,
        List<String> failureMessages,
        OrderAddressProjection deliveryAddress,
        List<BasketItemProjection> basket
) {
}
