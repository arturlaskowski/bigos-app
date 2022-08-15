package com.bigos.restaurant.adapters.orderprocessed.out.message.kafka.mapper;

import com.bigos.infrastructure.kafka.model.RestaurantApprovalMessageDto;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.restaurant.domain.event.OrderAcceptedEvent;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import org.springframework.stereotype.Component;

@Component
public class OutputMessagingKafkaDataMapper {

    public RestaurantApprovalEventDtoKafka orderAcceptedEventToRestaurantApprovalEventDtoKafka(OrderAcceptedEvent orderAcceptedEvent) {
        OrderProcessed order = orderAcceptedEvent.getOrderProcessed();
        RestaurantApprovalMessageDto restaurantApprovalMessageDto = RestaurantApprovalMessageDto.builder()
                .orderId(order.getId().id().toString())
                .restaurantId(order.getRestaurantId().id().toString())
                .orderApprovalStatus(order.getApprovalStatus().name())
                .build();
        return new RestaurantApprovalEventDtoKafka(restaurantApprovalMessageDto, order.getId().toString(), orderAcceptedEvent.getCreatedAt(), "toDo");
    }

    public RestaurantApprovalEventDtoKafka orderRejectedEventToRestaurantApprovalEventDtoKafka(OrderRejectedEvent orderRejectedEvent) {
        OrderProcessed order = orderRejectedEvent.getOrderProcessed();
        RestaurantApprovalMessageDto restaurantApprovalMessageDto = RestaurantApprovalMessageDto.builder()
                .orderId(order.getId().id().toString())
                .restaurantId(order.getRestaurantId().id().toString())
                .orderApprovalStatus(order.getApprovalStatus().name())
                .failureMessages(orderRejectedEvent.getFailureMessages())
                .build();
        return new RestaurantApprovalEventDtoKafka(restaurantApprovalMessageDto, order.getId().toString(), orderRejectedEvent.getCreatedAt(), "toDo");
    }
}
