package com.bigos.infrastructure.kafka.model;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderMessageDto(
        String orderId,
        String customerId,
        String restaurantId,
        BigDecimal price,
        List<BasketItemMessageDto> basket,
        OrderAddressDto deliveryAddress,
        String status,
        Instant creationDate,
        String failureMessages
) implements Serializable {
}
