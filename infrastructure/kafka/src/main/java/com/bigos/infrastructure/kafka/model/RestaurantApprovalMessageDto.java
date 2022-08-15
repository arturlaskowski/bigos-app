package com.bigos.infrastructure.kafka.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RestaurantApprovalMessageDto(
        String restaurantId,
        String orderId,
        String orderApprovalStatus,
        String failureMessages
) implements Serializable {
}
