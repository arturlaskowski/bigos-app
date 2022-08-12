package com.bigos.order.adapters.out.message.kafka.mapper;


import com.bigos.infrastructure.kafka.model.BasketItemMessageDto;
import com.bigos.infrastructure.kafka.model.OrderAddressDto;
import com.bigos.infrastructure.kafka.model.OrderEventDtoKafka;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessagingDataMapper {

    public OrderEventDtoKafka orderCretedEventToOrderEventDto(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        OrderMessageDto orderMessageDto = OrderMessageDto.builder()
                .orderId(order.getId().id().toString())
                .customerId(order.getCustomerId().id().toString())
                .restaurantId(order.getRestaurantId().id().toString())
                .price(order.getPrice().amount())
                .status(order.getStatus().name())
                .creationDate(order.getCreationDate())
                .deliveryAddress(orderDeliveryAddressToOrderAddressDto(order.getDeliveryAddress()))
                .basket(basketItemToBasketItemDto(order))
                .build();

        return new OrderEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), orderCreatedEvent.getCreatedAt());
    }

    private static OrderAddressDto orderDeliveryAddressToOrderAddressDto(OrderAddress orderAddress) {
        return new OrderAddressDto(orderAddress.getId().toString(), orderAddress.getStreet(), orderAddress.getPostCode(), orderAddress.getCity(), orderAddress.getHouseNo());
    }

    private static List<BasketItemMessageDto> basketItemToBasketItemDto(Order order) {
        return order.getBasket().stream()
                .map(item -> new BasketItemMessageDto(item.getItemNumber(), item.getProduct().getId().id().toString(), item.getProduct().getPrice().amount(),
                        item.getQuantity().numberOfElements(), item.getTotalPrice().amount())).toList();
    }
}
