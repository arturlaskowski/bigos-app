package com.bigos.order.adapters.model.mapper;

import com.bigos.common.domain.vo.*;
import com.bigos.order.adapters.model.entity.BasketItemEntity;
import com.bigos.order.adapters.model.entity.OrderAddressEntity;
import com.bigos.order.adapters.model.entity.OrderEntity;
import com.bigos.order.domain.model.BasketItem;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import com.bigos.order.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        return OrderEntity.builder()
                .id(order.getId().id())
                .customerId(order.getCustomerId().id())
                .restaurantId(order.getRestaurantId().id())
                .deliveryAddress(orderAddressToAddressEntity(order.getId().id(), order.getDeliveryAddress()))
                .price(order.getPrice().amount())
                .basket(basketItemsToBasketItemEntities(order.getId().id(), order.getBasket()))
                .status(order.getStatus())
                .creationDate(order.getCreationDate())
                .failureMessages(order.getFailureMessages())
                .build();
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
                .failureMessages(orderEntity.getFailureMessages())
                .build();
    }

    private List<BasketItem> basketItemEntitiesToBasketItems(Set<BasketItemEntity> items) {
        return items.stream()
                .map(orderItemEntity -> BasketItem.builder()
                        .orderId(new OrderId(orderItemEntity.getOrderId()))
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


    private Set<BasketItemEntity> basketItemsToBasketItemEntities(UUID orderId, List<BasketItem> items) {
        return items.stream()
                .map(orderItem -> BasketItemEntity.builder()
                        .itemNumber(orderItem.getItemNumber())
                        .orderId(orderId)
                        .productId(orderItem.getProduct().getId().id())
                        .totalPrice(orderItem.getTotalPrice().amount())
                        .quantity(orderItem.getQuantity().numberOfElements())
                        .price(orderItem.getProduct().getPrice().amount())
                        .totalPrice(orderItem.getTotalPrice().amount())
                        .build())
                .collect(Collectors.toSet());
    }

    private OrderAddressEntity orderAddressToAddressEntity(UUID orderId, OrderAddress orderAddress) {
        return OrderAddressEntity.builder()
                .id(orderAddress.getId())
                .street(orderAddress.getStreet())
                .postCode(orderAddress.getPostCode())
                .city(orderAddress.getCity())
                .houseNo(orderAddress.getHouseNo())
                .orderId(orderId)
                .build();
    }
}
