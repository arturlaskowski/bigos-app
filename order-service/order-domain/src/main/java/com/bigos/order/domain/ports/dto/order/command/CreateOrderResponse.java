package com.bigos.order.domain.ports.dto.order.command;

import java.util.UUID;

public record CreateOrderResponse(UUID orderId) {
}
