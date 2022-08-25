package com.bigos.order.adapters.in.message.kafka;

import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.order.adapters.exception.OrderNotFoundException;
import com.bigos.order.adapters.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;
import com.bigos.order.domain.ports.in.message.RestaurantApprovalEventListener;
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

    private final RestaurantApprovalEventListener restaurantApprovalEventListener;
    private final InputMessagingKafkaDataMapper mapper;

    public RestaurantApprovalKafkaEventListener(RestaurantApprovalEventListener restaurantApprovalEventListener, InputMessagingKafkaDataMapper mapper) {
        this.restaurantApprovalEventListener = restaurantApprovalEventListener;
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
                    restaurantApprovalEventListener.orderApproved(restaurantApprovalEvent);

                } else if (OrderApprovalStatus.REJECTED == restaurantApprovalEvent.orderApprovalStatus()) {
                    restaurantApprovalEventListener.orderRejected(restaurantApprovalEvent);
                }

            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking exception in RestaurantApprovalKafkaEventListener for order id: {}", orderId);
            } catch (OrderNotFoundException e) {
                log.error("Order not found, order id: {}", orderId);
            }
        });
    }
}
