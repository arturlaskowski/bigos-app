
package com.bigos.order.domain.ports.dto.outbox;

import com.bigos.common.domain.outbox.AbstractOutboxMessage;
import com.bigos.common.domain.saga.SagaStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrderOutboxMessage extends AbstractOutboxMessage {

    private SagaStatus sagaStatus;

    private Instant processedDate;
}