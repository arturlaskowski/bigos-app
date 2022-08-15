package com.bigos.restaurant.adapters.orderprocessed.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.restaurant.adapters.config.RestaurantServiceConfigProperties;
import com.bigos.restaurant.adapters.orderprocessed.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.restaurant.domain.event.OrderAcceptedEvent;
import com.bigos.restaurant.domain.ports.out.message.OrderAcceptedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderAcceptedKafkaEventPublisher implements OrderAcceptedMessagePublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher;
    private final RestaurantServiceConfigProperties properties;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public OrderAcceptedKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                            KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher,
                                            RestaurantServiceConfigProperties restaurantServiceConfigProperties,
                                            KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.kafkaPublisher = kafkaPublisher;
        this.properties = restaurantServiceConfigProperties;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderAcceptedEvent orderAcceptedEvent) {
        String orderId = orderAcceptedEvent.getOrderProcessed().getId().id().toString();

        try {
            RestaurantApprovalEventDtoKafka restaurantApprovalEventDtoKafka =
                    mapper.orderAcceptedEventToRestaurantApprovalEventDtoKafka(orderAcceptedEvent);
            kafkaPublisher.send(properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}