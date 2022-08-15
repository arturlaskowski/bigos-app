package com.bigos.restaurant.adapters.orderprocessed.in.message.kafka.mapper;

import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.restaurant.domain.ports.dto.OrderItemDto;
import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InputMessagingKafkaDataMapper {

    public OrderPaidEvent orderPaidEventDtoKafkaToOrderPaidEvent(OrderPaidEventDtoKafka orderPaidEventDtoKafka) {
        OrderMessageDto orderMessageDto = orderPaidEventDtoKafka.getData();
       return OrderPaidEvent.builder()
                .orderId(orderMessageDto.orderId())
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
