package com.bigos.payment.adapters.payment.outbox.model.entity;

import com.bigos.common.adapters.outbox.model.entity.OutboxEntity;
import com.bigos.common.domain.vo.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "payment_outbox")
@Getter
@Setter
public class PaymentOutboxEntity extends OutboxEntity {

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
