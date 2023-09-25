package com.bigos.order.application.outbox.dto;

import com.bigos.common.applciaiton.outbox.model.OutboxPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderEventPayload(
        @JsonProperty String orderId,
        @JsonProperty String customerId,
        @JsonProperty String restaurantId,
        @JsonProperty BigDecimal price,
        @JsonProperty String status,
        @JsonProperty List<BasketItemPayload> basket,
        @JsonProperty OrderAddressPayload deliveryAddress,
        @JsonProperty Instant creationDate,
        @JsonProperty String failureMessages
) implements OutboxPayload {
}
