package com.bigos.restaurant.domain.core;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.restaurant.domain.exception.RestaurantDomainException;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import org.junit.jupiter.api.Test;

import static com.bigos.restaurant.domain.OrderProcessedFixture.aCorrectOrderProcessed;
import static com.bigos.restaurant.domain.OrderProcessedFixture.aOrderProcessedWithWrongAmount;
import static org.junit.jupiter.api.Assertions.*;

class OrderProcessedTest {

    @Test
    void canInitialize() {
        //given
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        orderProcessed.initialize();
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
    }

    @Test
    void canApproveOrder() {
        //given
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        orderProcessed.accept();
        //then
        assertEquals(OrderApprovalStatus.ACCEPTED, orderProcessed.getApprovalStatus());
    }

    @Test
    void canRejectOrder() {
        //given
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        orderProcessed.reject();
        //then
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
    }

    @Test
    void shouldNotThrowExceptionForCorrectOrderAmount() {
        //given
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        orderProcessed.validate();
    }

    @Test
    void shouldNotBeValidForIncorrectOrderAmount() {
        //given
        OrderProcessed orderProcessed = aOrderProcessedWithWrongAmount();
        //when
        RestaurantDomainException restaurantDomainException = assertThrows(RestaurantDomainException.class,
                orderProcessed::validate);
        //then
        assertEquals("Order amount: 27.33 is different than total products price : 30.00", restaurantDomainException.getMessage());

    }
}