package com.bigos.order.adapters.out.message.kafka.mapper;


import com.bigos.infrastructure.kafka.model.*;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputMessagingKafkaDataMapper {

    public OrderCreatedEventDtoKafka orderCreatedEventToOrderEventDto(OrderCreatedEvent orderCreatedEvent) {
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

        return new OrderCreatedEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), orderCreatedEvent.getCreatedAt(),"toDo");
    }

    public OrderPaidEventDtoKafka orderPaidEventToOrderPaidEventDtoKafka(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
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

        return new OrderPaidEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), orderPaidEvent.getCreatedAt(), "toDo");
    }

    public OrderCancellingEventDtoKafka orderCancellingEventToOrderCancellingEventDtoKafka(OrderCancellingEvent orderCancellingEvent) {
        Order order = orderCancellingEvent.getOrder();
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

        return new OrderCancellingEventDtoKafka(orderMessageDto, orderMessageDto.orderId(), orderCancellingEvent.getCreatedAt(), "toDo");
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
