package com.bigos.restaurant.domain;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.orderprocessed.event.OrderAcceptedEvent;
import com.bigos.restaurant.domain.orderprocessed.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.orderprocessed.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.bigos.restaurant.domain.OrderProcessedFixture.aCorrectOrderProcessed;
import static com.bigos.restaurant.domain.OrderProcessedFixture.aOrderProcessedWithWrongAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestaurantDomainServiceTest {

    private final RestaurantDomainService restaurantDomainService = new RestaurantDomainService();

    @Test
    void shouldApproveOrder() {
        //given
        Restaurant restaurant = availableRestaurant();
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.acceptOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.ACCEPTED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderAcceptedEvent);
    }

    @Test
    void shouldRejectedOrderWhenRestaurantIsNotAvailable() {
        //given
        Restaurant restaurant = unAvailableRestaurant();
        OrderProcessed orderProcessed = aCorrectOrderProcessed();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.acceptOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderRejectedEvent);
        assertEquals("Restaurant is unavailable. " + restaurant.getId(),
                ((OrderRejectedEvent) orderProcessedEvent).getFailureMessages());
    }

    @Test
    void shouldRejectedOrderWhenOrderPriceIsDifferentThanOrderItemsTotal() {
        //given
        Restaurant restaurant = availableRestaurant();
        OrderProcessed orderProcessed = aOrderProcessedWithWrongAmount();
        //when
        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.acceptOrder(orderProcessed, restaurant);
        //then
        assertTrue(orderProcessed.getItems().stream().anyMatch(item -> item.getId() != null));
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
        assertTrue(orderProcessedEvent instanceof OrderRejectedEvent);
        assertEquals("Order amount: 27.33 is different than total products price : 30.00",
                ((OrderRejectedEvent) orderProcessedEvent).getFailureMessages());
    }

    private static Restaurant availableRestaurant() {
        return new Restaurant(new RestaurantId(UUID.randomUUID()), true, "Kup Kaszanke");
    }

    private static Restaurant unAvailableRestaurant() {
        return new Restaurant(new RestaurantId(UUID.randomUUID()), false, "Pod bykiem");
    }
}