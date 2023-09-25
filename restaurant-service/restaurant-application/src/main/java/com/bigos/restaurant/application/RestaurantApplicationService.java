package com.bigos.restaurant.application;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMapper;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;
import com.bigos.restaurant.application.orderprocessed.outbox.repository.OrderProcessedOutboxRepository;
import com.bigos.restaurant.domain.RestaurantDomainService;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import com.bigos.restaurant.domain.orderprocessed.event.OrderPaidEvent;
import com.bigos.restaurant.domain.orderprocessed.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.orderprocessed.port.OrderProcessedRepository;
import com.bigos.restaurant.domain.restaurant.port.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class RestaurantApplicationService {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantRepository restaurantRepository;
    private final OrderProcessedRepository orderProcessedRepository;
    private final OrderProcessedMapper orderProcessedMapper;
    private final OrderProcessedOutboxRepository outboxRepository;
    private final OrderProcessedOutboxMapper outboxMapper;

    public RestaurantApplicationService(RestaurantDomainService restaurantDomainService,
                                        RestaurantRepository restaurantRepository,
                                        OrderProcessedRepository orderProcessedRepository,
                                        OrderProcessedMapper orderProcessedMapper,
                                        OrderProcessedOutboxRepository outboxRepository,
                                        OrderProcessedOutboxMapper outboxMapper) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantRepository = restaurantRepository;
        this.orderProcessedRepository = orderProcessedRepository;
        this.orderProcessedMapper = orderProcessedMapper;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }

    @Transactional
    public void acceptOrder(OrderPaidEvent orderPaidEvent) {
        if (messageExistsInDatabase(orderPaidEvent)) {
            log.info("Outbox message already saved to database. sagaId: {}", orderPaidEvent.sageId());
            return;
        }

        Restaurant restaurant = restaurantRepository.getById(new RestaurantId(UUID.fromString(orderPaidEvent.restaurantId())));
        OrderProcessed orderProcessed = orderProcessedMapper.orderPaidEventToOrder(orderPaidEvent);

        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.acceptOrder(orderProcessed, restaurant);

        orderProcessedRepository.save(orderProcessed);

        saveOrderProcessedOutboxMessage(orderPaidEvent, orderProcessedEvent);
    }

    private boolean messageExistsInDatabase(OrderPaidEvent orderPaidEvent) {
        return outboxRepository.existByTypeAndSagaIdAndOutboxStatus(OrderProcessedOutboxMessage.class, UUID
                .fromString(orderPaidEvent.sageId()), OutboxStatus.COMPLETED);
    }

    private void saveOrderProcessedOutboxMessage(OrderPaidEvent orderPaidEvent, OrderProcessedEvent orderProcessedEvent) {
        OrderProcessedOutboxMessage orderProcessedOutboxMessage =
                outboxMapper.orderProcessedEventToOrderProcessedOutboxMessage(orderProcessedEvent, UUID.fromString(orderPaidEvent.sageId()));

        outboxRepository.save(orderProcessedOutboxMessage);
    }
}
