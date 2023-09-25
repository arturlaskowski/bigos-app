package com.bigos.restaurant.adapters.orderprocessed.message;

import com.bigos.infrastructure.kafka.config.producer.KafkaOutboxCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.restaurant.application.config.RestaurantServiceConfigProperties;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMapper;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMessage;
import com.bigos.restaurant.application.orderprocessed.outbox.publisher.OrderApprovalMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class OrderApprovalKafkaEventPublisher implements OrderApprovalMessagePublisher {

    private final OrderProcessedOutboxMapper mapper;
    private final KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher;
    private final RestaurantServiceConfigProperties properties;
    private final KafkaOutboxCallbackHelper kafkaCallbackHelper;

    public OrderApprovalKafkaEventPublisher(OrderProcessedOutboxMapper mapper,
                                            KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher,
                                            RestaurantServiceConfigProperties properties,
                                            KafkaOutboxCallbackHelper kafkaCallbackHelper) {
        this.mapper = mapper;
        this.kafkaPublisher = kafkaPublisher;
        this.properties = properties;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderProcessedOutboxMessage orderOutboxMessage, Consumer<OrderProcessedOutboxMessage> outboxCallback) {
        String orderId = orderOutboxMessage.getAggregateId().toString();
        String sagaId = orderOutboxMessage.getSagaId().toString();

        try {
            RestaurantApprovalEventDtoKafka restaurantApprovalEventDtoKafka =
                    mapper.orderProcessedOutboxMessageToRestaurantApprovalEventDtoKafka(orderOutboxMessage);
            kafkaPublisher.send(properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka.getClass().getSimpleName(),
                                    orderOutboxMessage, outboxCallback));

        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalEvent message to kafka. Order id: {}, Saga id: {} error: {}", orderId, sagaId, e.getMessage());
        }
    }
}
