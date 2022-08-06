package com.bigos.payment.adapters.payment.model.entity;

import com.bigos.payment.domain.model.vo.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "payments")
@Entity
public class PaymentEntity {

    @Id
    private UUID id;

    private UUID orderId;

    private UUID walletId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Instant createdDate;
}
