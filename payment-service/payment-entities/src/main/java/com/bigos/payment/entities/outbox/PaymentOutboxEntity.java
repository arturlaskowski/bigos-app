package com.bigos.payment.entities.outbox;

import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.common.entities.outbox.OutboxEntity;
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
