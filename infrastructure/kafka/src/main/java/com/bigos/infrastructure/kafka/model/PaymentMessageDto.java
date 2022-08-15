package com.bigos.infrastructure.kafka.model;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentMessageDto(
        String paymentId,
        String customerId,
        String orderId,
        BigDecimal price,
        Instant creationDate,
        String paymentStatus,
        String failureMessages
) implements Serializable {
}
