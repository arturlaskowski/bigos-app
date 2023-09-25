package com.bigos.order.application.command;

import com.bigos.common.domain.vo.*;
import com.bigos.order.application.command.dto.BasketItemDto;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.command.dto.OrderAddressDto;
import com.bigos.order.domain.core.BasketItem;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.core.OrderAddress;
import com.bigos.order.domain.core.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderCommandMapper {

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .deliveryAddress(orderAddressDtoToOrderAddress(createOrderCommand.address()))
                .price(new Money(createOrderCommand.price()))
                .basket(orderItemsToOrderItemEntities(createOrderCommand.items()))
                .build();
    }

    private List<BasketItem> orderItemsToOrderItemEntities(List<BasketItemDto> basketItems) {
        return basketItems.stream()
                .map(basketItem ->
                        BasketItem.builder()
                                .product(new Product(new ProductId(basketItem.productId()), new Money(basketItem.price())))
                                .quantity(new Quantity(basketItem.quantity()))
                                .totalPrice(new Money(basketItem.totalPrice()))
                                .build())
                .toList();
    }

    private OrderAddress orderAddressDtoToOrderAddress(OrderAddressDto orderAddressDto) {
        return new OrderAddress(
                UUID.randomUUID(),
                orderAddressDto.street(),
                orderAddressDto.postalCode(),
                orderAddressDto.city(),
                orderAddressDto.houseNo()
        );
    }
}
