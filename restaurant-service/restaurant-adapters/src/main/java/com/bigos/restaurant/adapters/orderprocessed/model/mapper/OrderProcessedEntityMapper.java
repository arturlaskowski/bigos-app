package com.bigos.restaurant.adapters.orderprocessed.model.mapper;

import com.bigos.common.domain.vo.*;
import com.bigos.restaurant.adapters.orderprocessed.model.entity.OrderItemEntity;
import com.bigos.restaurant.adapters.orderprocessed.model.entity.OrderProcessedEntity;
import com.bigos.restaurant.domain.model.OrderItem;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.vo.OrderItemId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderProcessedEntityMapper {

    public OrderProcessedEntity orderToOrderEntity(OrderProcessed orderProcessed) {
        return OrderProcessedEntity.builder()
                .id(orderProcessed.getId().id())
                .restaurantId(orderProcessed.getRestaurantId().id())
                .price(orderProcessed.getPrice().amount())
                .approvalStatus(orderProcessed.getApprovalStatus())
                .items(orderItemsToOrderItemsEntity(orderProcessed.getItems(), orderProcessed.getId()))
                .build();
    }

    public OrderProcessed orderEntityToOrder(OrderProcessedEntity orderProcessedEntity) {
        return OrderProcessed.builder()
                .id(new OrderId(orderProcessedEntity.getId()))
                .restaurantId(new RestaurantId(orderProcessedEntity.getRestaurantId()))
                .price(new Money(orderProcessedEntity.getPrice()))
                .approvalStatus(orderProcessedEntity.getApprovalStatus())
                .items(orderItemsEntityToOrderItems(orderProcessedEntity.getItems()))
                .build();
    }

    public OrderProcessed orderEntityToOrderWithoutItems(OrderProcessedEntity orderProcessedEntity) {
        return OrderProcessed.builder()
                .id(new OrderId(orderProcessedEntity.getId()))
                .restaurantId(new RestaurantId(orderProcessedEntity.getRestaurantId()))
                .price(new Money(orderProcessedEntity.getPrice()))
                .approvalStatus(orderProcessedEntity.getApprovalStatus())
                .build();
    }

    private Set<OrderItemEntity> orderItemsToOrderItemsEntity(List<OrderItem> items, OrderId orderId) {
        return items.stream()
                .map(orderItem ->
                        OrderItemEntity.builder()
                                .id(orderItem.getId().id())
                                .productId(orderItem.getProductId().id())
                                .price(orderItem.getPrice().amount())
                                .quantity(orderItem.getQuantity().numberOfElements())
                                .orderId(orderId.id())
                                .build()
                ).collect(Collectors.toSet());
    }

    private List<OrderItem> orderItemsEntityToOrderItems(Set<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                .map(orderItemEntity ->
                        OrderItem.builder()
                                .id(new OrderItemId(orderItemEntity.getId()))
                                .productId(new ProductId(orderItemEntity.getProductId()))
                                .price(new Money(orderItemEntity.getPrice()))
                                .quantity(new Quantity(orderItemEntity.getQuantity()))
                                .build()
                ).toList();
    }
}