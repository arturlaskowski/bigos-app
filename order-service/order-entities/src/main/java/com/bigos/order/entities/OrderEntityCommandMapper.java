package com.bigos.order.entities;

import com.bigos.common.domain.vo.*;
import com.bigos.order.domain.core.BasketItem;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.core.OrderAddress;
import com.bigos.order.domain.core.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderEntityCommandMapper {

    public static final String FAILURE_MESSAGE_DELIMITER = ";";

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().id())
                .customerId(order.getCustomerId().id())
                .restaurantId(order.getRestaurantId().id())
                .deliveryAddress(orderAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().amount())
                .basket(basketItemsToBasketItemEntities(order.getBasket()))
                .status(order.getStatus())
                .creationDate(order.getCreationDate())
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : null)
                .build();
        orderEntity.getDeliveryAddress().setOrder(orderEntity);
        orderEntity.getBasket().forEach(basketItemEntity -> basketItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .id(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getDeliveryAddress()))
                .price(new Money(orderEntity.getPrice()))
                .basket(basketItemEntitiesToBasketItems(orderEntity.getBasket()))
                .status(orderEntity.getStatus())
                .creationDate(orderEntity.getCreationDate())
                .failureMessages(orderEntity.getFailureMessages() != null && !orderEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))) : null)
                .build();
    }

    private List<BasketItem> basketItemEntitiesToBasketItems(Set<BasketItemEntity> items) {
        return items.stream()
                .map(orderItemEntity -> BasketItem.builder()
                        .orderId(new OrderId(orderItemEntity.getOrder().getId()))
                        .itemNumber(orderItemEntity.getItemNumber())
                        .product(new Product(new ProductId(orderItemEntity.getProductId()), new Money(orderItemEntity.getPrice())))
                        .quantity(new Quantity(orderItemEntity.getQuantity()))
                        .totalPrice(new Money(orderItemEntity.getTotalPrice()))
                        .build())
                .toList();
    }

    private OrderAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return new OrderAddress(address.getId(),
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                address.getHouseNo());
    }


    private Set<BasketItemEntity> basketItemsToBasketItemEntities(List<BasketItem> items) {
        return items.stream()
                .map(orderItem -> BasketItemEntity.builder()
                        .itemNumber(orderItem.getItemNumber())
                        .productId(orderItem.getProduct().getId().id())
                        .totalPrice(orderItem.getTotalPrice().amount())
                        .quantity(orderItem.getQuantity().numberOfElements())
                        .price(orderItem.getProduct().getPrice().amount())
                        .totalPrice(orderItem.getTotalPrice().amount())
                        .build())
                .collect(Collectors.toSet());
    }

    private OrderAddressEntity orderAddressToAddressEntity(OrderAddress orderAddress) {
        return OrderAddressEntity.builder()
                .id(orderAddress.getId())
                .street(orderAddress.getStreet())
                .postCode(orderAddress.getPostCode())
                .city(orderAddress.getCity())
                .houseNo(orderAddress.getHouseNo())
                .build();
    }
}
