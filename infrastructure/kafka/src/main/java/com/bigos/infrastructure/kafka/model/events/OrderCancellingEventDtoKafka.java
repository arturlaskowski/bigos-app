package com.bigos.infrastructure.kafka.model.events;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;

import java.time.Instant;

public class OrderCancellingEventDtoKafka extends MessageKafkaDto<OrderMessageDto> {

    public OrderCancellingEventDtoKafka(OrderMessageDto orderMessageDto, String itemId, Instant createdAt, String sageId) {
        super(itemId, createdAt, orderMessageDto, sageId);
    }
}
