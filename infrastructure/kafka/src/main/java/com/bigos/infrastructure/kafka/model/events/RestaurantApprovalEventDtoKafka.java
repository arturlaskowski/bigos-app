package com.bigos.infrastructure.kafka.model.events;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.RestaurantApprovalMessageDto;

import java.time.Instant;

public class RestaurantApprovalEventDtoKafka extends MessageKafkaDto<RestaurantApprovalMessageDto> {

    public RestaurantApprovalEventDtoKafka(RestaurantApprovalMessageDto restaurantApprovalMessageDto, String itemId, Instant createdAt, String sageId) {
        super(itemId, createdAt, restaurantApprovalMessageDto, sageId);
    }
}
