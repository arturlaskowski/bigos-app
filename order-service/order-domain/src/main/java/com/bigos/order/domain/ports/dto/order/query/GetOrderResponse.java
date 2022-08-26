package com.bigos.order.domain.ports.dto.order.query;

import com.bigos.common.domain.vo.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetOrderResponse(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        OrderStatus status,
        BigDecimal price,
        List<BasketItemGetDto> items,
        OrderAddressGetDto address,
        List<String> failureMessages
) {
}
