package com.bigos.restaurant.adapters.orderprocessed.in.message;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.adapters.orderprocessed.in.message.mapper.OrderProcessedMapper;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;
import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;
import com.bigos.restaurant.domain.ports.in.message.OrderApprovalListener;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedRepository;
import com.bigos.restaurant.domain.ports.out.repository.RestaurantRepository;
import com.bigos.restaurant.domain.service.RestaurantDomainService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderApprovalListenerImpl implements OrderApprovalListener {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantRepository restaurantRepository;
    private final OrderProcessedRepository orderProcessedRepository;
    private final OrderProcessedMapper orderProcessedMapper;

    public OrderApprovalListenerImpl(RestaurantDomainService restaurantDomainService, RestaurantRepository restaurantRepository, OrderProcessedRepository orderProcessedRepository, OrderProcessedMapper orderProcessedMapper) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantRepository = restaurantRepository;
        this.orderProcessedRepository = orderProcessedRepository;
        this.orderProcessedMapper = orderProcessedMapper;
    }

    @Override
    public void approveOrder(OrderPaidEvent orderPaidEvent) {
        Restaurant restaurant = restaurantRepository.getById(new RestaurantId(UUID.fromString(orderPaidEvent.restaurantId())));
        OrderProcessed orderProcessed = orderProcessedMapper.orderPaidEventToOrder(orderPaidEvent);

        restaurantDomainService.verifyOrder(orderProcessed, restaurant);

        orderProcessedRepository.save(orderProcessed);
    }
}
