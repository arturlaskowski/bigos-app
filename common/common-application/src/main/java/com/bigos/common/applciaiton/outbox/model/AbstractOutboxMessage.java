package com.bigos.common.applciaiton.outbox.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AbstractOutboxMessage<P extends OutboxPayload> {

    private UUID id;

    private Instant createdDate;

    private Instant sendDate;

    private UUID aggregateId;

    private String aggregateName;

    private UUID sagaId;

    private OutboxStatus outboxStatus;

    private P payload;

    private int version;

}
