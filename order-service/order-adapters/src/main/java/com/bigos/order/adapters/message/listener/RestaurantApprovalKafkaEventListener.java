package com.bigos.order.adapters.message.listener;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.order.application.exception.OrderNotFoundException;
import com.bigos.order.application.saga.OrderApprovalSaga;
import com.bigos.order.domain.event.RestaurantApprovalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalKafkaEventListener implements KafkaConsumer<RestaurantApprovalEventDtoKafka> {

    private final OrderApprovalSaga orderApprovalSaga;
    private final InputMessagingKafkaDataMapper mapper;

    public RestaurantApprovalKafkaEventListener(OrderApprovalSaga orderApprovalSaga, InputMessagingKafkaDataMapper mapper) {
        this.orderApprovalSaga = orderApprovalSaga;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(topics = "${order-service.restaurant-approval-events-topic-name}")
    public void receive(@Payload List<RestaurantApprovalEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received RestaurantApprovalEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(restaurantApprovalKafkaDto -> {
            String orderId = restaurantApprovalKafkaDto.getData().orderId();
            try {
                RestaurantApprovalEvent restaurantApprovalEvent = mapper.restaurantApprovalEventDtoKafkaToRestaurantApprovalEvent(restaurantApprovalKafkaDto);
                if (OrderApprovalStatus.ACCEPTED == restaurantApprovalEvent.orderApprovalStatus()) {
                    orderApprovalSaga.process(restaurantApprovalEvent);
                    log.info("Order with id: {} is approved", restaurantApprovalEvent.orderId());

                } else if (OrderApprovalStatus.REJECTED == restaurantApprovalEvent.orderApprovalStatus()) {
                    orderApprovalSaga.rollback(restaurantApprovalEvent);
                    log.info("Order with id: {} is start cancelling, failure messages: {}",
                            restaurantApprovalEvent.orderId(), restaurantApprovalEvent.failureMessages());
                }

            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking exception in RestaurantApprovalKafkaEventListener for order id: {}", orderId);
            } catch (OrderNotFoundException e) {
                log.error("Order not found, order id: {}", orderId);
            }
        });
    }
}
