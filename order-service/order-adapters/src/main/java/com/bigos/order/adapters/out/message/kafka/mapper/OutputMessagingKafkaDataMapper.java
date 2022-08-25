package com.bigos.order.adapters.out.message.kafka.mapper;


import com.bigos.infrastructure.kafka.model.BasketItemMessageDto;
import com.bigos.infrastructure.kafka.model.OrderAddressMessageDto;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.order.adapters.outbox.dto.BasketItemPayload;
import com.bigos.order.adapters.outbox.dto.OrderAddressPayload;
import com.bigos.order.adapters.outbox.dto.OrderEventPayload;
import com.bigos.order.domain.ports.dto.outbox.OrderCancellingOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputMessagingKafkaDataMapper {

    public OrderCreatedEventDtoKafka orderCreatedOutboxMessageToOrderCreatedEventDtoKafka(OrderCreatedOutboxMessage outboxMessage) {
        OrderEventPayload messagePayload = (OrderEventPayload) outboxMessage.getPayload();

        OrderMessageDto orderMessageDto = OrderMessageDto.builder()
                .orderId(messagePayload.orderId())
                .customerId(messagePayload.customerId())
                .restaurantId(messagePayload.restaurantId())
                .price(messagePayload.price())
                .status(messagePayload.status())
                .creationDate(messagePayload.creationDate())
                .deliveryAddress(orderAddressPayloadToOrderAddressDto(messagePayload.deliveryAddress()))
                .basket(basketItemPayloadToBasketItemDto(messagePayload.basket()))
                .build();

        return new OrderCreatedEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), outboxMessage.getCreatedDate(), outboxMessage.getSagaId().toString());
    }

    public OrderPaidEventDtoKafka orderPaidOutboxMessageToOrderPaidEventDtoKafka(OrderPaidOutboxMessage outboxMessage) {
        OrderEventPayload messagePayload = (OrderEventPayload) outboxMessage.getPayload();

        OrderMessageDto orderMessageDto = OrderMessageDto.builder()
                .orderId(messagePayload.orderId())
                .customerId(messagePayload.customerId())
                .restaurantId(messagePayload.restaurantId())
                .price(messagePayload.price())
                .status(messagePayload.status())
                .creationDate(messagePayload.creationDate())
                .deliveryAddress(orderAddressPayloadToOrderAddressDto(messagePayload.deliveryAddress()))
                .basket(basketItemPayloadToBasketItemDto(messagePayload.basket()))
                .build();
        return new OrderPaidEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), outboxMessage.getCreatedDate(), outboxMessage.getSagaId().toString());
    }

    public OrderCancellingEventDtoKafka orderCancellingOutboxMessageToOrderCancellingEventDtoKafka(OrderCancellingOutboxMessage outboxMessage) {
        OrderEventPayload messagePayload = (OrderEventPayload) outboxMessage.getPayload();

        OrderMessageDto orderMessageDto = OrderMessageDto.builder()
                .orderId(messagePayload.orderId())
                .customerId(messagePayload.customerId())
                .restaurantId(messagePayload.restaurantId())
                .price(messagePayload.price())
                .status(messagePayload.status())
                .creationDate(messagePayload.creationDate())
                .deliveryAddress(orderAddressPayloadToOrderAddressDto(messagePayload.deliveryAddress()))
                .basket(basketItemPayloadToBasketItemDto(messagePayload.basket()))
                .build();
        return new OrderCancellingEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), outboxMessage.getCreatedDate(), outboxMessage.getSagaId().toString());

    }

    private static List<BasketItemMessageDto> basketItemPayloadToBasketItemDto(List<BasketItemPayload> basketItemPayloads) {
        return basketItemPayloads.stream()
                .map(payload -> new BasketItemMessageDto(payload.itemNumber(), payload.productId(), payload.price(),
                        payload.quantity(), payload.totalPrice())).toList();
    }

    private static OrderAddressMessageDto orderAddressPayloadToOrderAddressDto(OrderAddressPayload orderAddressPayload) {
        return new OrderAddressMessageDto(orderAddressPayload.orderAddressId(), orderAddressPayload.street(), orderAddressPayload.postCode(),
                orderAddressPayload.city(), orderAddressPayload.houseNo());
    }
}
