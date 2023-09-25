package com.bigos.restaurant.application;

import com.bigos.common.domain.vo.*;
import com.bigos.restaurant.domain.orderprocessed.core.OrderItem;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.orderprocessed.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class OrderProcessedMapper {

    public OrderProcessed orderPaidEventToOrder(OrderPaidEvent orderPaidEvent) {
        return OrderProcessed.builder()
                .id(new OrderId(UUID.fromString(orderPaidEvent.orderId())))
                .restaurantId(new RestaurantId(UUID.fromString(orderPaidEvent.restaurantId())))
                .price(new Money(orderPaidEvent.price()))
                .items(orderItemsDtoToOrderItems(orderPaidEvent))
                .build();
    }

    private static List<OrderItem> orderItemsDtoToOrderItems(OrderPaidEvent orderPaidEvent) {
        return orderPaidEvent.orderItems().stream().map(item -> OrderItem.builder()
                .productId(new ProductId(UUID.fromString(item.productId())))
                .quantity(new Quantity(item.quantity()))
                .price(new Money(item.price()))
                .build()).toList();
    }
}
