package com.bigos.order.domain.event;

import com.bigos.common.domain.vo.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentStatusEvent(
        String id,
        String sagaId,
        Instant createdAt,
        String orderId,
        String paymentId,
        String customerId,
        BigDecimal price,
        PaymentStatus paymentStatus,
        String failureMessages
) {
}
