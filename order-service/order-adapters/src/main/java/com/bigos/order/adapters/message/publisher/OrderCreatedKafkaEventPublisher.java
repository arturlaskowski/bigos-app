package com.bigos.order.adapters.message.publisher;

import com.bigos.infrastructure.kafka.config.producer.KafkaOutboxCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.order.application.config.OrderServiceConfigProperties;
import com.bigos.order.application.outbox.dto.OrderCreatedOutboxMessage;
import com.bigos.order.application.outbox.publisher.OrderCreatedEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class OrderCreatedKafkaEventPublisher implements OrderCreatedEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderCreatedEventDtoKafka> kafkaPublisher;
    private final KafkaOutboxCallbackHelper kafkaCallbackHelper;

    public OrderCreatedKafkaEventPublisher(OutputMessagingKafkaDataMapper mapper,
                                           OrderServiceConfigProperties properties,
                                           KafkaPublisher<OrderCreatedEventDtoKafka> kafkaPublisher,
                                           KafkaOutboxCallbackHelper kafkaCallbackHelper) {
        this.mapper = mapper;
        this.properties = properties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    public void publish(OrderCreatedOutboxMessage outboxMessage, Consumer<OrderCreatedOutboxMessage> outboxCallback) {
        String orderId = outboxMessage.getAggregateId().toString();
        String sagaId = outboxMessage.getSagaId().toString();
        try {
            OrderCreatedEventDtoKafka orderCreatedEventDtoKafka = mapper.orderCreatedOutboxMessageToOrderCreatedEventDtoKafka(outboxMessage);

            kafkaPublisher.send(properties.getOrderCreatedEventsTopicName(), sagaId, orderCreatedEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderCreatedEventsTopicName(), orderId, orderCreatedEventDtoKafka.getClass().getSimpleName(),
                                    outboxMessage, outboxCallback));

        } catch (Exception e) {
            log.error("Error while sending OrderCreatedEvent message to kafka. Order id: {} and SagaId: {} error: {}"
                    , orderId, sagaId, e.getMessage());
        }
    }
}