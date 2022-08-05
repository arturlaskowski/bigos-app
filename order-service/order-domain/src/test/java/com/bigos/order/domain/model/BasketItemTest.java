package com.bigos.order.domain.model;


import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.ProductId;
import com.bigos.common.domain.vo.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasketItemTest {

    @Test
    void priceValidationTest() {
        //expect
        assertTrue(aBasketItem(new BigDecimal(1), 18, new BigDecimal(18)).isValidPrice());
        assertTrue(aBasketItem(new BigDecimal(5), 5, new BigDecimal(25)).isValidPrice());
        assertTrue(aBasketItem(new BigDecimal(14.5), 21, new BigDecimal(304.5)).isValidPrice());

        assertFalse(aBasketItem(new BigDecimal(1), 1, new BigDecimal(2)).isValidPrice());
        assertFalse(aBasketItem(new BigDecimal(5), 3, new BigDecimal(25)).isValidPrice());
        assertFalse(aBasketItem(new BigDecimal(8.5), 2, new BigDecimal(16)).isValidPrice());
    }

    private static BasketItem aBasketItem(BigDecimal productCost, Integer quantity, BigDecimal totalPrice) {
        return BasketItem.builder()
                .product(new Product(new ProductId(UUID.randomUUID()), new Money(productCost)))
                .quantity(new Quantity(quantity))
                .totalPrice(new Money(totalPrice))
                .build();
    }
}