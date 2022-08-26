package com.bigos.order.domain.ports.dto.order.query;

import java.util.UUID;

public record GetOrderQuery(UUID orderId) {
}
