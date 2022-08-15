package com.bigos.infrastructure.kafka.model.events;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;

import java.time.Instant;

public class OrderCreatedEventDtoKafka extends MessageKafkaDto<OrderMessageDto> {

    public OrderCreatedEventDtoKafka(OrderMessageDto orderMessageDto, String itemId, Instant createdAt, String sagaId) {
        super(itemId, createdAt, orderMessageDto, sagaId);
    }
}
