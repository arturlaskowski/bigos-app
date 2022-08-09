package com.bigos.restaurant.domain;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.Quantity;
import com.bigos.restaurant.domain.model.OrderItem;
import com.bigos.restaurant.domain.model.OrderProcessed;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderProcessedFixture {

    public static OrderProcessed aCorrectOrderProcessed() {
        return aOrderProcessed(new BigDecimal("30.60"),
                OrderItem.builder()
                        .price(new Money(new BigDecimal("15.30")))
                        .quantity(new Quantity(2)).build());

    }

    public static OrderProcessed aOrderProcessedWithWrongAmount() {
        return aOrderProcessed(new BigDecimal("27.33"),
                OrderItem.builder()
                        .price(new Money(new BigDecimal("15.00")))
                        .quantity(new Quantity(2)).build());

    }

    public static OrderProcessed aOrderProcessed(BigDecimal price, OrderItem... orderItems) {
        return OrderProcessed.builder()
                .id(new OrderId(UUID.randomUUID()))
                .price(new Money(price))
                .items(List.of(orderItems))
                .build();
    }
}
