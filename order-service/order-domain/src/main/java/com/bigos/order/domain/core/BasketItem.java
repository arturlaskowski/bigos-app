package com.bigos.order.domain.core;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.Quantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BasketItem {

    private Integer itemNumber;

    private OrderId orderId;

    private Product product;

    private Quantity quantity;

    private Money totalPrice;

    void initializeBasketItem(OrderId orderId, Integer itemNumber) {
        this.orderId = orderId;
        this.itemNumber = itemNumber;
    }

    boolean isValidPrice() {
        return product.getPrice().isGreaterThanZero() &&
                product.getPrice().multiply(quantity.numberOfElements()).equals(totalPrice);
    }
}
