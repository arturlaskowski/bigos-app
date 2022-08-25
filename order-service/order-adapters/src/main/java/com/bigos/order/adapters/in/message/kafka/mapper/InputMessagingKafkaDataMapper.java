package com.bigos.order.adapters.in.message.kafka.mapper;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.infrastructure.kafka.model.PaymentMessageDto;
import com.bigos.infrastructure.kafka.model.RestaurantApprovalMessageDto;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;
import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;
import org.springframework.stereotype.Component;

@Component
public class InputMessagingKafkaDataMapper {

    public PaymentStatusEvent paymentStatusEventDtoKafkaToPaymentStatusEvent(PaymentStatusEventDtoKafka paymentStatusEventDtoKafka) {
        PaymentMessageDto paymentMessageDto = paymentStatusEventDtoKafka.getData();
        return PaymentStatusEvent.builder()
                .id(paymentStatusEventDtoKafka.getMessageId())
                .sagaId(paymentStatusEventDtoKafka.getSagaId())
                .createdAt(paymentStatusEventDtoKafka.getCreatedAt())
                .paymentId(paymentMessageDto.paymentId())
                .orderId(paymentMessageDto.orderId())
                .customerId(paymentMessageDto.customerId())
                .paymentStatus(PaymentStatus.valueOf(paymentMessageDto.paymentStatus()))
                .price(paymentMessageDto.price())
                .failureMessages(paymentMessageDto.failureMessages())
                .build();
    }

    public RestaurantApprovalEvent restaurantApprovalEventDtoKafkaToRestaurantApprovalEvent(RestaurantApprovalEventDtoKafka restaurantApprovalEventDtoKafka) {
        RestaurantApprovalMessageDto restaurantApprovalMessageDto = restaurantApprovalEventDtoKafka.getData();
        return RestaurantApprovalEvent.builder()
                .id(restaurantApprovalEventDtoKafka.getMessageId())
                .sagaId(restaurantApprovalEventDtoKafka.getSagaId())
                .createdAt(restaurantApprovalEventDtoKafka.getCreatedAt())
                .restaurantId(restaurantApprovalMessageDto.restaurantId())
                .orderId(restaurantApprovalMessageDto.orderId())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(restaurantApprovalMessageDto.orderApprovalStatus()))
                .failureMessages(restaurantApprovalMessageDto.failureMessages())
                .build();
    }
}
