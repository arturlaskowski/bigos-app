package com.bigos.restaurant.domain.service;

import com.bigos.restaurant.domain.event.OrderAcceptedEvent;
import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.exception.RestaurantDomainException;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    @Override
    public OrderProcessedEvent verifyOrder(OrderProcessed orderProcessed, Restaurant restaurant) {
        try {
            orderProcessed.initialize();
            restaurant.validate();
            orderProcessed.validate();
            orderProcessed.accept();
            log.info("Order with id: {} is approved.", orderProcessed.getId().id());

            return new OrderAcceptedEvent(orderProcessed, Instant.now());
        } catch (RestaurantDomainException e) {
            orderProcessed.reject();
            log.info("Order with id: {} is rejected. Reason: {}", orderProcessed.getId().id(), e.getMessage());

            return new OrderRejectedEvent(orderProcessed, Instant.now(), e.getMessage());
        }
    }
}
