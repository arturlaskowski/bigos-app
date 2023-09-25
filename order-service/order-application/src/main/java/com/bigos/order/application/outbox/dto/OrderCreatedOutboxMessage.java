package com.bigos.order.application.outbox.dto;

import com.bigos.order.application.outbox.OrderOutboxMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreatedOutboxMessage extends OrderOutboxMessage {

}