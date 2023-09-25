package com.bigos.order.domain.core;


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
        //given
        var orderPaid = aOrder(OrderStatus.PAID);
        var orderApproved = aOrder(OrderStatus.APPROVED);
        var orderCancelling = aOrder(OrderStatus.CANCELLING);
        var orderCancelled = aOrder(OrderStatus.CANCELLED);

        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderPaid::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderApproved::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderCancelling::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderCancelled::pay);
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
        //given
        var orderPending = aOrder(OrderStatus.PENDING);
        var orderApproved = aOrder(OrderStatus.APPROVED);
        var orderCancelling = aOrder(OrderStatus.CANCELLING);
        var orderCancelled = aOrder(OrderStatus.CANCELLED);

        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderPending::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderApproved::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderCancelling::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderCancelled::approve);
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
        //given
        var orderPending = aOrder(OrderStatus.PENDING);
        var orderApproved = aOrder(OrderStatus.APPROVED);
        var orderCancelling = aOrder(OrderStatus.CANCELLING);
        var orderCancelled = aOrder(OrderStatus.CANCELLED);

        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderPending.startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderApproved.startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderCancelling.startCancelling(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderCancelled.startCancelling(""));
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
        //given
        var orderPaid = aOrder(OrderStatus.PAID);
        var orderApproved = aOrder(OrderStatus.APPROVED);
        var orderCancelled = aOrder(OrderStatus.CANCELLED);

        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderPaid.cancel(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderApproved.cancel(""));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderCancelled.cancel(""));
    }

    @Test
    void canMakeCancelOperationWhenOrderIsInCancellingState() {
        //given
        Order order = aOrder(OrderStatus.CANCELLING);

        //when
        order.cancel("");

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
        assertEquals("Restauration reject order", order.getFailureMessages().get(0));

        //when
        order.cancel("Payment rejected");

        //then
        assertEquals("Payment rejected", order.getFailureMessages().get(1));
    }

    @Test
    void orderPriceMustBeGreaterThanZero() {
        //given
        var order = aInitializerOrder(new BigDecimal(-3), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));

        //expect
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(order::validatePrice);
    }

    @Test
    void orderPriceMustBeTheSameLikeSumOfBasketItemsPrice() {
        //given
        var orderWithIncorrectTotalPrice = aInitializerOrder(new BigDecimal(19), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        var orderWithIncorrectBasketItemPrice = aInitializerOrder(new BigDecimal(20), aBasketItem(new BigDecimal(10), 1, new BigDecimal(20)));
        var orderWithTotalPriceSmallerThenZero = aInitializerOrder(new BigDecimal(-20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));

        var orderWithCorrectPrice = aInitializerOrder(new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));

        //when
        var incorrectTotalPriceException = assertThrows(OrderDomainException.class, orderWithIncorrectTotalPrice::validatePrice);
        var incorrectBasketItemPriceException = assertThrows(OrderDomainException.class, orderWithIncorrectBasketItemPrice::validatePrice);
        var totalPriceSmallerThenZeroException = assertThrows(OrderDomainException.class, orderWithTotalPriceSmallerThenZero::validatePrice);

        orderWithCorrectPrice.validatePrice();

        //then
        assertEquals("Total order price: 19.00 is different than basket items total: 20.00", incorrectTotalPriceException.getMessage());
        assertEquals("Incorrect basket item price: 20.00", incorrectBasketItemPriceException.getMessage());
        assertEquals("Order price: -20.00 must be greater than zero", totalPriceSmallerThenZeroException.getMessage());
    }
}