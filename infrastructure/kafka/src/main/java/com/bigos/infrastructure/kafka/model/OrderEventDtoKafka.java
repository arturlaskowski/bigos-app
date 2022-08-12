package com.bigos.infrastructure.kafka.model;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;

import java.time.Instant;

public class OrderEventDtoKafka extends MessageKafkaDto<OrderMessageDto> {

    public OrderEventDtoKafka(OrderMessageDto orderMessageDto, String itemId, Instant createdAt) {
        super(itemId, createdAt, orderMessageDto);
    }
}
