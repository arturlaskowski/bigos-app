package com.bigos.order.domain.model;


import com.bigos.common.domain.vo.OrderStatus;
import com.bigos.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.bigos.order.domain.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void canInitializeOrder() {
        //given
        Order order = aOrder(new BigDecimal("42.26"), aBasketItem(new BigDecimal("10.00"), 2, new BigDecimal("20.00")),
                aBasketItem(new BigDecimal("15.63"), 2, new BigDecimal("31.26")));

        //when
        order.initialize();

        //then
        assertTrue(order.isPendingStatus());
        assertNotNull(order.getId());
        assertNotNull(order.getCreationDate());
        assertEquals(1, order.getBasket().get(0).getItemNumber());
        assertEquals(2, order.getBasket().get(1).getItemNumber());
    }

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
        order.startCancelling("Restauration reject order");

        //then
        assertEquals("Restauration reject order", order.getFailureMessages());
    }

    @Test
    void orderPriceMustBeGreaterThanZero() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(-3), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validatePrice());
    }

    @Test
    void orderPriceMustBeTheSameLikeSumOfBasketItemsPrice() {
        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(19), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validatePrice());

        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() ->
                aOrderWithInicializacion(new BigDecimal(20), aBasketItem(new BigDecimal(10), 1, new BigDecimal(20))).validatePrice());

        aOrderWithInicializacion(new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20))).validatePrice();
    }
}