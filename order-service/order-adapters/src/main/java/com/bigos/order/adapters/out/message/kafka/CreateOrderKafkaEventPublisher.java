package com.bigos.order.adapters.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.order.adapters.config.OrderServiceConfigProperties;
import com.bigos.order.adapters.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.ports.out.message.OrderCreatedEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaEventPublisher implements OrderCreatedEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderCreatedEventDtoKafka> kafkaPublisher;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public CreateOrderKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                          OrderServiceConfigProperties orderServiceConfigProperties,
                                          KafkaPublisher<OrderCreatedEventDtoKafka> kafkaPublisher,
                                          KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.properties = orderServiceConfigProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().id().toString();
        try {
            OrderCreatedEventDtoKafka orderCreatedEventDtoKafka = mapper.orderCreatedEventToOrderEventDto(domainEvent);

            kafkaPublisher.send(properties.getOrderCreatedEventsTopicName(), orderId, orderCreatedEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderCreatedEventsTopicName(), orderId, orderCreatedEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending OrderCreatedEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
