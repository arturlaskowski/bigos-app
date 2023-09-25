package com.bigos.order.adapters.rest.dto;

import com.bigos.common.domain.vo.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetOrderDetailsResponse(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        OrderStatus status,
        BigDecimal price,
        List<BasketItemDto> items,
        OrderAddressDto address,
        List<String> failureMessages
) {

    public record OrderAddressDto(
            UUID id,
            String street,
            String postalCode,
            String city,
            String houseNo
    ) {
    }

    public record BasketItemDto(
            Integer itemNumber,
            UUID productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal totalPrice) {
    }
}
