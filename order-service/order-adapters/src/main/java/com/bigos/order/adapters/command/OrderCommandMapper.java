package com.bigos.order.adapters.command;

import com.bigos.common.domain.vo.*;
import com.bigos.order.domain.model.BasketItem;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import com.bigos.order.domain.model.Product;
import com.bigos.order.domain.ports.dto.create.BasketItemCreateDto;
import com.bigos.order.domain.ports.dto.create.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.create.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.create.OrderAddressCreateDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class OrderCommandMapper {

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .deliveryAddress(orderAddressDtoToOrderAddress(createOrderCommand.address()))
                .price(new Money(createOrderCommand.price()))
                .basket(orderItemsToOrderItemEntities(createOrderCommand.items()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return new CreateOrderResponse(order.getId().id());
    }

    private List<BasketItem> orderItemsToOrderItemEntities(List<BasketItemCreateDto> basketItems) {
        return basketItems.stream()
                .map(basketItem ->
                        BasketItem.builder()
                                .product(new Product(new ProductId(basketItem.productId()), new Money(basketItem.price())))
                                .quantity(new Quantity(basketItem.quantity()))
                                .totalPrice(new Money(basketItem.totalPrice()))
                                .build())
                .toList();
    }

    private OrderAddress orderAddressDtoToOrderAddress(OrderAddressCreateDto orderAddressCreateDto) {
        return new OrderAddress(
                UUID.randomUUID(),
                orderAddressCreateDto.street(),
                orderAddressCreateDto.postalCode(),
                orderAddressCreateDto.city(),
                orderAddressCreateDto.houseNo()
        );
    }
}
