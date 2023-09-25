package com.bigos.payment.application.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CancelPaymentCommand(
        UUID id,
        UUID sagaId,
        UUID orderId,
        UUID customerId,
        BigDecimal price
) {
}
