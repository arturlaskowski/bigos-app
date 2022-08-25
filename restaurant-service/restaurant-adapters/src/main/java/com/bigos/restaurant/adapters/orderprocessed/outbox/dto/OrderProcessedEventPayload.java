package com.bigos.restaurant.adapters.orderprocessed.outbox.dto;

import com.bigos.common.domain.outbox.OutboxPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OrderProcessedEventPayload(
        @JsonProperty String orderId,
        @JsonProperty String restaurantId,
        @JsonProperty String orderApprovalStatus,
        @JsonProperty String failureMessages
) implements OutboxPayload {
}
