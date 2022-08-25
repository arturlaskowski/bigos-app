package com.bigos.restaurant.adapters.orderprocessed.in.message;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.adapters.orderprocessed.in.message.mapper.OrderProcessedMapper;
import com.bigos.restaurant.adapters.orderprocessed.outbox.dto.mapper.OrderProcessedOutboxMapper;
import com.bigos.restaurant.domain.event.OrderProcessedEvent;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.Restaurant;
import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;
import com.bigos.restaurant.domain.ports.dto.outbox.OrderProcessedOutboxMessage;
import com.bigos.restaurant.domain.ports.in.message.OrderPaidEventListener;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedOutboxRepository;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedRepository;
import com.bigos.restaurant.domain.ports.out.repository.RestaurantRepository;
import com.bigos.restaurant.domain.service.RestaurantDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class OrderPaidEventListenerImpl implements OrderPaidEventListener {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantRepository restaurantRepository;
    private final OrderProcessedRepository orderProcessedRepository;
    private final OrderProcessedMapper orderProcessedMapper;
    private final OrderProcessedOutboxRepository outboxRepository;
    private final OrderProcessedOutboxMapper outboxMapper;

    public OrderPaidEventListenerImpl(RestaurantDomainService restaurantDomainService,
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

    @Override
    @Transactional
    public void acceptOrder(OrderPaidEvent orderPaidEvent) {

        if (messageExistsInDatabase(orderPaidEvent)) {
            log.info("Outbox message already saved to database. sagaId: {}", orderPaidEvent.sageId());
            return;
        }

        Restaurant restaurant = restaurantRepository.getById(new RestaurantId(UUID.fromString(orderPaidEvent.restaurantId())));
        OrderProcessed orderProcessed = orderProcessedMapper.orderPaidEventToOrder(orderPaidEvent);

        OrderProcessedEvent orderProcessedEvent = restaurantDomainService.verifyOrder(orderProcessed, restaurant);

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
