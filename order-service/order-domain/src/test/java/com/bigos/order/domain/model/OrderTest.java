package com.bigos.order.domain.model;


import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.ProductId;
import com.bigos.common.domain.vo.Quantity;
import com.bigos.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void cannotMakePayOperationWhenOrderIsNotInPendingState() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.PAID).pay());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.APPROVED).pay());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLING).pay());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLED).pay());
    }

    @Test
    void canMakePayOperationWhenOrderIsInPendingState() {
        //given
        Order order = aOrder(OrderStatus.PENDING);

        //when
        order.pay();

        //then
        assertTrue(order.isPaidStatus());
    }

    @Test
    void cannotMakeApprovedOperationWhenOrderIsNotInPaidState() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.PENDING).approve());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.APPROVED).approve());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLING).approve());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLED).approve());
    }

    @Test
    void canMakeApprovedOperationWhenOrderIsInPaidState() {
        //given
        Order order = aOrder(OrderStatus.PAID);

        //when
        order.approve();

        //then
        assertTrue(order.isApprovedStatus());
    }

    @Test
    void cannotMakeIntCancelOperationWhenOrderIsNotInPaidState() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.PENDING).startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.APPROVED).startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLING).startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLED).startCancelling(""));
    }

    @Test
    void canMakeIntCancelOperationWhenOrderIsInPaidState() {
        //given
        Order order = aOrder(OrderStatus.PAID);

        //when
        order.startCancelling("");

        //then
        assertTrue(order.isCancellingStatus());
    }

    @Test
    void cannotMakeCancelOperationWhenOrderIsNotInCancellingState() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.PENDING).cancel());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.PAID).cancel());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.APPROVED).cancel());
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> aOrder(OrderStatus.CANCELLED).cancel());
    }

    @Test
    void canMakeCancelOperationWhenOrderIsInCancellingState() {
        //given
        Order order = aOrder(OrderStatus.CANCELLING);

        //when
        order.cancel();

        //then
        assertTrue(order.isCancelledStatus());
    }

    @Test
    void canAddMessageToFailureMessages() {
        //given
        Order order = aOrder(OrderStatus.PAID);

        //when
        order.startCancelling("Restauration reject orde");

        //then
        assertEquals("Restauration reject orde", order.getFailureMessages());
    }

    @Test
    void orderMustHaveStatus() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrder(new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validate());
    }

    @Test
    void orderPriceMustBeGreaterThanZero() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(-3), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validate());
    }

    @Test
    void orderPriceMustBeTheSameLikeSumOfBasketItemsPrice() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(19), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validate());

        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(20), aBasketItem(new BigDecimal(10), 1, new BigDecimal(20))).validate());

        aOrderWithInicializacion(new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validate();
    }

    @Test
    void canInitializeOrder() {
        //given
        Order order = aOrder(new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));

        //when
        order.initialize();

        //then
        assertTrue(order.isPendingStatus());

    }

    private static Order aOrder(OrderStatus status) {
        return Order.builder().status(status).build();
    }

    private static Order aOrderWithInicializacion(BigDecimal price, BasketItem... basketItem) {
        Order order = aOrder(price, basketItem);
        order.initialize();
        return order;
    }

    private static Order aOrder(BigDecimal price, BasketItem... basketItem) {
        return Order.builder()
                .basket(List.of(basketItem))
                .price(new Money(price))
                .build();
    }

    private static BasketItem aBasketItem(BigDecimal productCost, Integer quantity, BigDecimal totalPrice) {
        return BasketItem.builder()
                .product(new Product(new ProductId(UUID.randomUUID()), new Money(productCost)))
                .quantity(new Quantity(quantity))
                .totalPrice(new Money(totalPrice))
                .build();
    }
}