package com.bigos.order.adapters.outbox.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BasketItemPayload(
        @JsonProperty Integer itemNumber,
        @JsonProperty String productId,
        @JsonProperty BigDecimal price,
        @JsonProperty Integer quantity,
        @JsonProperty BigDecimal totalPrice
) {
}
