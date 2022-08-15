package com.bigos.infrastructure.kafka.model.events;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.PaymentMessageDto;

import java.time.Instant;

public class PaymentStatusEventDtoKafka extends MessageKafkaDto<PaymentMessageDto> {

    public PaymentStatusEventDtoKafka(PaymentMessageDto paymentMessageDto, String itemId, Instant createdAt, String sageId) {
        super(itemId, createdAt, paymentMessageDto, sageId);
    }
}
