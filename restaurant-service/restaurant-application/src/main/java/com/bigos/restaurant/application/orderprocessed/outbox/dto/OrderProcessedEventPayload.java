package com.bigos.restaurant.application.orderprocessed.outbox.dto;

import com.bigos.common.applciaiton.outbox.model.OutboxPayload;
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
