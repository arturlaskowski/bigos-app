package com.bigos.order.adapters.outbox.model.entity;

import com.bigos.common.adapters.outbox.model.entity.OutboxEntity;
import com.bigos.common.domain.saga.SagaStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "order_outbox")
@Getter
@Setter
public class OrderOutboxEntity extends OutboxEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus sagaStatus;

    private Instant processedDate;
}
