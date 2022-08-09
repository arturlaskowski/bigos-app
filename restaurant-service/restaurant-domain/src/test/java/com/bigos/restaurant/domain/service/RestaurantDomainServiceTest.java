package com.bigos.restaurant.domain.service;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.event.OrderApprovedEvent;
import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;
import com.bigos.restaurant.domain.model.vo.OrderApprovalStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.bigos.restaurant.domain.OrderProcessedFixture.aCorrectOrderProcessed;
import static com.bigos.restaurant.domain.OrderProcessedFixture.aOrderProcessedWithWrongAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestaurantDomainServiceTest {

    private final RestaurantDomainService restaurantDomainService = new RestaurantDomainServiceImpl();

    @Test
    void shouldApproveOrder() {
        //given
        Restaurant restaurant = availableRestaurant();
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.verifyOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.APPROVED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderApprovedEvent);
    }

    @Test
    void shouldRejectedOrderWhenRestaurantIsNotAvailable() {
        //given
        Restaurant restaurant = unAvailableRestaurant();
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.verifyOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderRejectedEvent);
        assertEquals("Restaurant is unavailable. " + restaurant.getRestaurantId(),
                ((OrderRejectedEvent) orderProcessedEvent).getFailureMessage());
    }

    @Test
    void shouldRejectedOrderWhenOrderPriceIsDifferentThanOrderItemsTotal() {
        //given
        Restaurant restaurant = availableRestaurant();
        OrderProcessed orderProcessed = aOrderProcessedWithWrongAmount();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.verifyOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderRejectedEvent);
        assertEquals("Order amount: 27.33 is different than total products price : 30.00",
                ((OrderRejectedEvent) orderProcessedEvent).getFailureMessage());
    }

    private static Restaurant availableRestaurant() {
        return new Restaurant(new RestaurantId(UUID.randomUUID()), true, "Kup Kaszanke");
    }

    private static Restaurant unAvailableRestaurant() {
        return new Restaurant(new RestaurantId(UUID.randomUUID()), false, "Pod bykiem");
    }
}