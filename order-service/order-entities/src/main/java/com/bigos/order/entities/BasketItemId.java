package com.bigos.order.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class BasketItemId implements Serializable {

    private Integer itemNumber;

    private OrderEntity order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketItemId that = (BasketItemId) o;
        return itemNumber.equals(that.itemNumber) && order.equals(that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemNumber, order);
    }

}
