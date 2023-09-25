package com.bigos.order.entities.outbox;

import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.common.entities.outbox.OutboxEntity;
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
