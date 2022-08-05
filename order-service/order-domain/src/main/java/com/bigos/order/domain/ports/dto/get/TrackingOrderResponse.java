package com.bigos.order.domain.ports.dto.get;

import com.bigos.order.domain.model.OrderStatus;

import java.util.UUID;

public record TrackingOrderResponse(
        UUID orderId,
        OrderStatus orderStatus,
        String failureMessage) {
}
