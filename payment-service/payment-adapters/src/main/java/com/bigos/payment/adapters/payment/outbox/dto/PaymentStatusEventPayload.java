package com.bigos.payment.adapters.payment.outbox.dto;

import com.bigos.common.domain.outbox.OutboxPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentStatusEventPayload(
        @JsonProperty String paymentId,
        @JsonProperty String customerId,
        @JsonProperty String orderId,
        @JsonProperty BigDecimal price,
        @JsonProperty Instant createdDate,
        @JsonProperty String paymentStatus,
        @JsonProperty String failureMessages
) implements OutboxPayload {
}
