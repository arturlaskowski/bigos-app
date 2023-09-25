package com.bigos.restaurant.adapters.orderprocessed.message;

import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.restaurant.domain.orderprocessed.event.OrderItemDto;
import com.bigos.restaurant.domain.orderprocessed.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class InputMessagingKafkaDataMapper {

    public OrderPaidEvent orderPaidEventDtoKafkaToOrderPaidEvent(OrderPaidEventDtoKafka orderPaidEventDtoKafka) {
        OrderMessageDto orderMessageDto = orderPaidEventDtoKafka.getData();
        return OrderPaidEvent.builder()
                .orderId(orderMessageDto.orderId())
                .sageId(orderPaidEventDtoKafka.getSagaId())
                .restaurantId(orderMessageDto.restaurantId())
                .price(orderMessageDto.price())
                .status(orderMessageDto.status())
                .orderItems(basketItemMessageDtoToOrderItemDto(orderMessageDto))
                .build();
    }

    private static List<OrderItemDto> basketItemMessageDtoToOrderItemDto(OrderMessageDto orderMessageDto) {
        return orderMessageDto.basket().stream().map(item -> new OrderItemDto(item.productId(), item.price(), item.quantity())).toList();
    }

}
