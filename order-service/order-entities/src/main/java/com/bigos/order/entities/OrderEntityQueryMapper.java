package com.bigos.order.entities;

import com.bigos.order.application.query.BasketItemProjection;
import com.bigos.order.application.query.OrderAddressProjection;
import com.bigos.order.application.query.OrderProjection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class OrderEntityQueryMapper {

    public static final String FAILURE_MESSAGE_DELIMITER = ";";

    public OrderProjection orderEntityToOrderProjection(OrderEntity orderEntity) {
        return OrderProjection.builder()
                .id(orderEntity.getId())
                .customerId(orderEntity.getCustomerId())
                .restaurantId(orderEntity.getRestaurantId())
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getDeliveryAddress()))
                .price(orderEntity.getPrice())
                .basket(basketItemEntitiesToBasketItems(orderEntity.getBasket()))
                .status(orderEntity.getStatus())
                .creationDate(orderEntity.getCreationDate())
                .failureMessages(orderEntity.getFailureMessages() != null && !orderEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))) : null)
                .build();
    }

    private List<BasketItemProjection> basketItemEntitiesToBasketItems(Set<BasketItemEntity> items) {
        return items.stream()
                .map(orderItemEntity -> BasketItemProjection.builder()
                        .itemNumber(orderItemEntity.getItemNumber())
                        .productId(orderItemEntity.getProductId())
                        .quantity(orderItemEntity.getQuantity())
                        .totalPrice(orderItemEntity.getTotalPrice())
                        .price(orderItemEntity.getPrice())
                        .build())
                .toList();
    }

    private OrderAddressProjection addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return new OrderAddressProjection(address.getId(),
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                address.getHouseNo());
    }
}
