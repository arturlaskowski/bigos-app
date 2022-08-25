package com.bigos.order.adapters.outbox.dto.mapper;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.order.adapters.outbox.dto.BasketItemPayload;
import com.bigos.order.adapters.outbox.dto.OrderAddressPayload;
import com.bigos.order.adapters.outbox.dto.OrderEventPayload;
import com.bigos.order.adapters.saga.SagaStatusMapper;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.model.BasketItem;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import com.bigos.order.domain.ports.dto.outbox.OrderCancellingOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderOutboxMapper {

    private final SagaStatusMapper sagaStatusMapper;

    public OrderOutboxMapper(SagaStatusMapper sagaStatusMapper) {
        this.sagaStatusMapper = sagaStatusMapper;
    }

    public OrderCreatedOutboxMessage orderCreatedEventToOrderCreatedOutboxMessage(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();

        OrderCreatedOutboxMessage orderCreatedOutboxMessage = new OrderCreatedOutboxMessage();
        orderCreatedOutboxMessage.setId(UUID.randomUUID());
        orderCreatedOutboxMessage.setCreatedDate(orderCreatedEvent.getCreatedAt());
        orderCreatedOutboxMessage.setAggregateId(order.getId().id());
        orderCreatedOutboxMessage.setAggregateName(order.getClass().getSimpleName());
        orderCreatedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderCreatedOutboxMessage.setSagaId(UUID.randomUUID());
        orderCreatedOutboxMessage.setSagaStatus(sagaStatusMapper.orderStatusToSagaStatus(order.getStatus()));
        orderCreatedOutboxMessage.setPayload(orderToOrderEventPayload(order));

        return orderCreatedOutboxMessage;
    }


    public OrderPaidOutboxMessage orderPaidEventToOrderPaidOutboxMessage(OrderPaidEvent orderPaidEvent, UUID sagaId) {
        Order order = orderPaidEvent.getOrder();
        OrderPaidOutboxMessage orderPaidOutboxMessage = new OrderPaidOutboxMessage();
        orderPaidOutboxMessage.setId(UUID.randomUUID());
        orderPaidOutboxMessage.setCreatedDate(orderPaidEvent.getCreatedAt());
        orderPaidOutboxMessage.setAggregateId(order.getId().id());
        orderPaidOutboxMessage.setAggregateName(order.getClass().getSimpleName());
        orderPaidOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderPaidOutboxMessage.setSagaId(sagaId);
        orderPaidOutboxMessage.setSagaStatus(sagaStatusMapper.orderStatusToSagaStatus(order.getStatus()));
        orderPaidOutboxMessage.setPayload(orderToOrderEventPayload(order));

        return orderPaidOutboxMessage;
    }

    public OrderCancellingOutboxMessage orderCancellingEventToOrderCancellingOutboxMessage(OrderCancellingEvent orderCancellingEvent, UUID sagaId) {
        Order order = orderCancellingEvent.getOrder();
        OrderCancellingOutboxMessage orderCancellingOutboxMessage = new OrderCancellingOutboxMessage();
        orderCancellingOutboxMessage.setId(UUID.randomUUID());
        orderCancellingOutboxMessage.setCreatedDate(orderCancellingEvent.getCreatedAt());
        orderCancellingOutboxMessage.setAggregateId(order.getId().id());
        orderCancellingOutboxMessage.setAggregateName(order.getClass().getSimpleName());
        orderCancellingOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderCancellingOutboxMessage.setSagaId(sagaId);
        orderCancellingOutboxMessage.setSagaStatus(sagaStatusMapper.orderStatusToSagaStatus(order.getStatus()));
        orderCancellingOutboxMessage.setPayload(orderToOrderEventPayload(order));

        return orderCancellingOutboxMessage;
    }

    private static OrderEventPayload orderToOrderEventPayload(Order order) {
        return OrderEventPayload.builder()
                .orderId(order.getId().id().toString())
                .customerId(order.getCustomerId().id().toString())
                .restaurantId(order.getRestaurantId().id().toString())
                .creationDate(order.getCreationDate())
                .price(order.getPrice().amount())
                .status(order.getStatus().name())
                .basket(orderBasketItemsToBasketItemPayload(order.getBasket()))
                .deliveryAddress(orderAddressToOrderAddressPayload(order.getDeliveryAddress()))
                .build();
    }

    private static OrderAddressPayload orderAddressToOrderAddressPayload(OrderAddress address) {
        return OrderAddressPayload.builder()
                .orderAddressId(address.getId().toString())
                .street(address.getStreet())
                .postCode(address.getPostCode())
                .city(address.getCity())
                .houseNo(address.getHouseNo())
                .build();
    }

    private static List<BasketItemPayload> orderBasketItemsToBasketItemPayload(List<BasketItem> basketItems) {
        return basketItems.stream().map(basketItem -> BasketItemPayload.builder()
                .itemNumber(basketItem.getItemNumber())
                .productId(basketItem.getProduct().getId().id().toString())
                .price(basketItem.getProduct().getPrice().amount())
                .quantity(basketItem.getQuantity().numberOfElements())
                .totalPrice(basketItem.getTotalPrice().amount())
                .build()).toList();
    }
}
