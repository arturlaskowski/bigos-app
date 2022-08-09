package com.bigos.restaurant.domain.service;

import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;

public interface RestaurantDomainService {

    OrderProcessedEvent verifyOrder(OrderProcessed orderProcessed, Restaurant restaurant);
}
