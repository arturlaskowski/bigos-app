package com.bigos.order.domain;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.ProductId;
import com.bigos.common.domain.vo.Quantity;
import com.bigos.order.domain.core.BasketItem;
import com.bigos.order.domain.core.Order;
import com.bigos.common.domain.vo.OrderStatus;
import com.bigos.order.domain.core.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order aOrder(BigDecimal price, BasketItem... basketItem) {
        return Order.builder()
                .basket(List.of(basketItem))
                .price(new Money(price))
                .build();
    }

    public static Order aOrder(OrderStatus status, BigDecimal price, BasketItem... basketItem) {
        return Order.builder()
                .status(status)
                .basket(List.of(basketItem))
                .price(new Money(price))
                .build();
    }

    public static BasketItem aBasketItem(BigDecimal productCost, Integer quantity, BigDecimal totalPrice) {
        return BasketItem.builder()
                .product(new Product(new ProductId(UUID.randomUUID()), new Money(productCost)))
                .quantity(new Quantity(quantity))
                .totalPrice(new Money(totalPrice))
                .build();
    }

    public static Order aOrder(OrderStatus status) {
        return Order.builder().status(status).build();
    }

    public static Order aInitializerOrder(BigDecimal price, BasketItem... basketItem) {
        Order order = aOrder(price, basketItem);
        order.initialize();
        return order;
    }
}
