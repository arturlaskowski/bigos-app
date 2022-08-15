package com.bigos.restaurant.adapters.orderprocessed.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.RestaurantApprovalEventDtoKafka;
import com.bigos.restaurant.adapters.config.RestaurantServiceConfigProperties;
import com.bigos.restaurant.adapters.orderprocessed.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.restaurant.domain.event.OrderRejectedEvent;
import com.bigos.restaurant.domain.ports.out.message.OrderRejectedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderRejectedKafkaEventPublisher implements OrderRejectedMessagePublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher;
    private final RestaurantServiceConfigProperties properties;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public OrderRejectedKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                            KafkaPublisher<RestaurantApprovalEventDtoKafka> kafkaPublisher,
                                            RestaurantServiceConfigProperties restaurantServiceConfigProperties,
                                            KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.kafkaPublisher = kafkaPublisher;
        this.properties = restaurantServiceConfigProperties;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }


    @Override
    public void publish(OrderRejectedEvent orderRejectedEvent) {
        String orderId = orderRejectedEvent.getOrderProcessed().getId().id().toString();

        try {
            RestaurantApprovalEventDtoKafka restaurantApprovalEventDtoKafka =
                    mapper.orderRejectedEventToRestaurantApprovalEventDtoKafka(orderRejectedEvent);
            kafkaPublisher.send(properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getRestaurantApprovalEventsTopicName(), orderId, restaurantApprovalEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }

}
