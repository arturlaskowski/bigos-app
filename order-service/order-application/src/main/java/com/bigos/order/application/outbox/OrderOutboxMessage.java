
package com.bigos.order.application.outbox;

import com.bigos.common.applciaiton.outbox.model.AbstractOutboxMessage;
import com.bigos.common.applciaiton.saga.SagaStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrderOutboxMessage extends AbstractOutboxMessage {

    private SagaStatus sagaStatus;

    private Instant processedDate;
}