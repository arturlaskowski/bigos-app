package com.bigos.payment.adapters.payment.message.listener;

import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.payment.application.payment.dto.CancelPaymentCommand;
import com.bigos.payment.application.payment.dto.MakePaymentCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class InputMessagingKafkaDataMapper {

    public MakePaymentCommand orderEventDtoToMakePaymentCommand(OrderCreatedEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new MakePaymentCommand(UUID.fromString(orderEventDto.getMessageId()), UUID.fromString(orderEventDto.getSagaId()),
                UUID.fromString(orderDto.orderId()), UUID.fromString(orderDto.customerId()), orderDto.price());
    }

    public CancelPaymentCommand orderEventDtoToCancelPaymentCommand(OrderCancellingEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new CancelPaymentCommand(UUID.fromString(orderEventDto.getMessageId()), UUID.fromString(orderEventDto.getSagaId()),
                UUID.fromString(orderDto.orderId()), UUID.fromString(orderDto.customerId()), orderDto.price());
    }
}
