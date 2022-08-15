package com.bigos.restaurant.adapters.orderprocessed.in.message;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.adapters.orderprocessed.in.message.mapper.OrderProcessedMapper;
import com.bigos.restaurant.domain.event.OrderAcceptedEvent;
import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;
import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;
import com.bigos.restaurant.domain.ports.in.message.OrderPaidListener;
import com.bigos.restaurant.domain.ports.out.message.OrderAcceptedMessagePublisher;
import com.bigos.restaurant.domain.ports.out.message.OrderRejectedMessagePublisher;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedRepository;
import com.bigos.restaurant.domain.ports.out.repository.RestaurantRepository;
import com.bigos.restaurant.domain.service.RestaurantDomainService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderPaidListenerImpl implements OrderPaidListener {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantRepository restaurantRepository;
    private final OrderProcessedRepository orderProcessedRepository;
    private final OrderProcessedMapper orderProcessedMapper;
    private final OrderAcceptedMessagePublisher orderAcceptedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public OrderPaidListenerImpl(RestaurantDomainService restaurantDomainService, RestaurantRepository restaurantRepository, OrderProcessedRepository orderProcessedRepository, OrderProcessedMapper orderProcessedMapper, OrderAcceptedMessagePublisher orderAcceptedMessagePublisher, OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantRepository = restaurantRepository;
        this.orderProcessedRepository = orderProcessedRepository;
        this.orderProcessedMapper = orderProcessedMapper;
        this.orderAcceptedMessagePublisher = orderAcceptedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Override
    public void acceptOrder(OrderPaidEvent orderPaidEvent) {
        Restaurant restaurant = restaurantRepository.getById(new RestaurantId(UUID.fromString(orderPaidEvent.restaurantId())));
        OrderProcessed orderProcessed = orderProcessedMapper.orderPaidEventToOrder(orderPaidEvent);

        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.verifyOrder(orderProcessed, restaurant);

        orderProcessedRepository.save(orderProcessed);

        if (orderProcessed.isAccepted()) {
            orderAcceptedMessagePublisher.publish((OrderAcceptedEvent) orderProcessedEvent);

        } else if (orderProcessed.isRejected()) {
            orderRejectedMessagePublisher.publish((OrderRejectedEvent) orderProcessedEvent);
        }
    }
}
