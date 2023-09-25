package com.bigos.order.domain;

import com.bigos.common.domain.vo.OrderStatus;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.bigos.order.domain.OrderFixture.aBasketItem;
import static com.bigos.order.domain.OrderFixture.aOrder;
import static org.junit.jupiter.api.Assertions.*;

class OrderDomainServiceTest {

    private final OrderDomainService orderDomainService = new OrderDomainService();

    @Test
    void shouldCreateOrder() {
        //given
        Order order = aOrder(new BigDecimal("51.26"), aBasketItem(new BigDecimal("10.00"), 2, new BigDecimal("20.00")),
                aBasketItem(new BigDecimal("15.63"), 2, new BigDecimal("31.26")));
        //when
        OrderCreatedEvent orderCreatedEvent = orderDomainService.create(order);
        //then
        assertTrue(order.isPendingStatus());
        assertNotNull(order.getId());
        assertNotNull(order.getCreationDate());
        assertEquals(1, order.getBasket().get(0).getItemNumber());
        assertEquals(2, order.getBasket().get(1).getItemNumber());
        assertNotNull(orderCreatedEvent.getCreatedAt());
        assertNotNull(orderCreatedEvent.getOrder());
    }

    @Test
    void cannotCreateOrderWhenOrderPriceIsNotGreaterThanZero() {
        //given
        Order order = aOrder(new BigDecimal(-34), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        //when
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderDomainService.create(order));
        //then
        assertEquals("Order price: -34.00 must be greater than zero", orderDomainException.getMessage());
    }

    @Test
    void cannotCreateOrderWhenOrderPriceIsDifferentThanBasketItemsTotal() {
        //given
        Order order = aOrder(new BigDecimal("42.26"), aBasketItem(new BigDecimal("10.00"), 2, new BigDecimal("20.00")),
                aBasketItem(new BigDecimal("15.63"), 2, new BigDecimal("31.26")));
        //when
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderDomainService.create(order));
        //then
        assertEquals("Total order price: 42.26 is different than basket items total: 51.26", orderDomainException.getMessage());
    }

    @Test
    void shouldPayOrder() {
        //given
        Order order = aOrder(OrderStatus.PENDING, new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        //when
        OrderPaidEvent orderPaidEvent = orderDomainService.pay(order);
        //then
        assertTrue(order.isPaidStatus());
        assertNotNull(orderPaidEvent.getCreatedAt());
        assertNotNull(orderPaidEvent.getOrder());
    }

    @Test
    void shouldApproveOrder() {
        //given
        Order order = aOrder(OrderStatus.PAID, new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        //when
        orderDomainService.approve(order);
        //then
        assertTrue(order.isApprovedStatus());
    }

    @Test
    void shouldStartCancellingOrder() {
        //given
        Order order = aOrder(OrderStatus.PAID, new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        //when
        OrderCancellingEvent orderCancellingEvent = orderDomainService.startCancelling(order, "Restauration not active");
        //then
        assertTrue(order.isCancellingStatus());
        assertEquals("Restauration not active", order.getFailureMessages().get(0));
        assertNotNull(orderCancellingEvent.getCreatedAt());
        assertNotNull(orderCancellingEvent.getOrder());
    }

    @Test
    void shouldCancelOrderOrder() {
        //given
        Order order = aOrder(OrderStatus.CANCELLING, new BigDecimal(20), aBasketItem(new BigDecimal(10), 2, new BigDecimal(20)));
        //when
        orderDomainService.cancelOrder(order, "");
        //then
        assertTrue(order.isCancelledStatus());
    }
}