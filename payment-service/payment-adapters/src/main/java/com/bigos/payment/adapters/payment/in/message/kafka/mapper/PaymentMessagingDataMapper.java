package com.bigos.payment.adapters.payment.in.message.kafka.mapper;

import com.bigos.infrastructure.kafka.model.OrderEventDtoKafka;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.payment.domain.ports.dto.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.MakePaymentCommand;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessagingDataMapper {

    public MakePaymentCommand orderEventDtoToMakePaymentCommand(OrderEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new MakePaymentCommand(orderEventDto.getMessageId(), orderDto.orderId(), orderDto.customerId(), orderDto.price());
    }

    public CancelPaymentCommand orderEventDtoToCancelPaymentCommand(OrderEventDtoKafka orderEventDto) {
        OrderMessageDto orderDto = orderEventDto.getData();
        return new CancelPaymentCommand(orderEventDto.getMessageId(), orderDto.orderId(), orderDto.customerId(), orderDto.price());
    }
}
