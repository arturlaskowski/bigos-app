package com.bigos.order.domain.ports.dto.restaurant;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RestaurantApprovalEvent(
        String id,
        String sagaId,
        Instant createdAt,
        String orderId,
        String restaurantId,
        OrderApprovalStatus orderApprovalStatus,
        String failureMessages
) {
}
