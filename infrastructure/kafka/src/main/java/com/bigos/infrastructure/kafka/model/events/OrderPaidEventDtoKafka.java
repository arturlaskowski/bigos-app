package com.bigos.infrastructure.kafka.model.events;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;

import java.time.Instant;

public class OrderPaidEventDtoKafka extends MessageKafkaDto<OrderMessageDto> {

    public OrderPaidEventDtoKafka(OrderMessageDto orderMessageDto, String itemId, Instant createdAt, String sageId) {
        super(itemId, createdAt, orderMessageDto, sageId);
    }
}
