package com.bigos.payment.adapters.payment.in.message.kafka.mapper;

import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.payment.domain.ports.dto.payment.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.payment.MakePaymentCommand;
import org.springframework.stereotype.Component;

@Component
public class InputMessagingKafkaDataMapper {

    public MakePaymentCommand orderEventDtoToMakePaymentCommand(OrderCreatedEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new MakePaymentCommand(orderEventDto.getMessageId(), orderEventDto.getSagaId(), orderDto.orderId(), orderDto.customerId(), orderDto.price());
    }

    public CancelPaymentCommand orderEventDtoToCancelPaymentCommand(OrderCancellingEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new CancelPaymentCommand(orderEventDto.getMessageId(), orderEventDto.getSagaId(), orderDto.orderId(), orderDto.customerId(), orderDto.price());
    }
}
