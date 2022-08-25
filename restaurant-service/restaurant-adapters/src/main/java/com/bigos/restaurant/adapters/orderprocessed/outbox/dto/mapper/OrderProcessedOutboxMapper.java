package com.bigos.restaurant.adapters.orderprocessed.outbox.dto.mapper;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.infrastructure.kafka.model.RestaurantApprovalMessageDto;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.restaurant.adapters.orderprocessed.outbox.dto.OrderProcessedEventPayload;
import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderProcessedOutboxMapper {

    public OrderProcessedOutboxMessage orderProcessedEventToOrderProcessedOutboxMessage(OrderProcessedEvent orderProcessedEvent, UUID sagaId) {
        OrderProcessed orderProcessed = orderProcessedEvent.getOrderProcessed();

        OrderProcessedOutboxMessage orderProcessedOutboxMessage = new OrderProcessedOutboxMessage();
        orderProcessedOutboxMessage.setAggregateName(orderProcessed.getClass().getSimpleName());
        orderProcessedOutboxMessage.setAggregateId(orderProcessed.getId().id());
        orderProcessedOutboxMessage.setId(UUID.randomUUID());
        orderProcessedOutboxMessage.setCreatedDate(orderProcessedEvent.getCreatedAt());
        orderProcessedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderProcessedOutboxMessage.setSagaId(sagaId);
        orderProcessedOutboxMessage.setApprovalStatus(orderProcessed.getApprovalStatus());

        if (orderProcessedEvent instanceof OrderRejectedEvent) {
            orderProcessedOutboxMessage.setPayload(orderRejectedEventToOrderProcessedEventPayload((OrderRejectedEvent) orderProcessedEvent, orderProcessed));
        } else {
            orderProcessedOutboxMessage.setPayload(orderProcessedEventToOrderProcessedEventPayload(orderProcessed));
        }
        return orderProcessedOutboxMessage;
    }

    private static OrderProcessedEventPayload orderProcessedEventToOrderProcessedEventPayload(OrderProcessed orderProcessed) {
        return OrderProcessedEventPayload.builder()
                .orderId(orderProcessed.getId().id().toString())
                .restaurantId(orderProcessed.getRestaurantId().id().toString())
                .orderApprovalStatus(orderProcessed.getApprovalStatus().name())
                .build();
    }

    private static OrderProcessedEventPayload orderRejectedEventToOrderProcessedEventPayload(OrderRejectedEvent orderProcessedEvent, OrderProcessed orderProcessed) {
        return OrderProcessedEventPayload.builder()
                .orderId(orderProcessed.getId().id().toString())
                .restaurantId(orderProcessed.getRestaurantId().id().toString())
                .orderApprovalStatus(orderProcessed.getApprovalStatus().name())
                .failureMessages(orderProcessedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalEventDtoKafka orderProcessedOutboxMessageToRestaurantApprovalEventDtoKafka(OrderProcessedOutboxMessage orderOutboxMessage) {
        OrderProcessedEventPayload payload = (OrderProcessedEventPayload) orderOutboxMessage.getPayload();
        RestaurantApprovalMessageDto restaurantApprovalMessageDto = RestaurantApprovalMessageDto.builder()
                .orderId(payload.orderId())
                .restaurantId(payload.restaurantId())
                .orderApprovalStatus(payload.orderApprovalStatus())
                .failureMessages(payload.failureMessages())
                .build();
        return new RestaurantApprovalEventDtoKafka(restaurantApprovalMessageDto, payload.orderId(), orderOutboxMessage.getCreatedDate(), orderOutboxMessage.getSagaId().toString());
    }
}
