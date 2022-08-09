package com.bigos.restaurant.domain.model;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.ProductId;
import com.bigos.common.domain.vo.Quantity;
import com.bigos.restaurant.domain.model.vo.OrderItemId;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class OrderItem {

    private OrderItemId id;

    private ProductId productId;

    private Money price;

    private Quantity quantity;

    public void initialize() {
        id = new OrderItemId(UUID.randomUUID());
    }

}
