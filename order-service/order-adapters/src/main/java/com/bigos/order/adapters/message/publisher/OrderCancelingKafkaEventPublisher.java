package com.bigos.order.adapters.message.publisher;


import com.bigos.infrastructure.kafka.config.producer.KafkaOutboxCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.order.application.config.OrderServiceConfigProperties;
import com.bigos.order.application.outbox.dto.OrderCancellingOutboxMessage;
import com.bigos.order.application.outbox.publisher.OrderCancellingEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class OrderCancelingKafkaEventPublisher implements OrderCancellingEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderCancellingEventDtoKafka> kafkaPublisher;
    private final KafkaOutboxCallbackHelper kafkaCallbackHelper;

    public OrderCancelingKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                             OrderServiceConfigProperties orderServiceConfigProperties,
                                             KafkaPublisher<OrderCancellingEventDtoKafka> kafkaPublisher,
                                             KafkaOutboxCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.properties = orderServiceConfigProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    public void publish(OrderCancellingOutboxMessage outboxMessage, Consumer<OrderCancellingOutboxMessage> outboxCallback) {
        String orderId = outboxMessage.getAggregateId().toString();
        String sagaId = outboxMessage.getSagaId().toString();
        try {
            OrderCancellingEventDtoKafka orderCancellingEventDtoKafka = mapper.orderCancellingOutboxMessageToOrderCancellingEventDtoKafka(outboxMessage);

            kafkaPublisher.send(properties.getOrderCancellingEventsTopicName(), sagaId, orderCancellingEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderCancellingEventsTopicName(), orderId, orderCancellingEventDtoKafka.getClass().getSimpleName(),
                                    outboxMessage, outboxCallback));

        } catch (Exception e) {
            log.error("Error while sending OrderCancellingEvent message to kafka. Order id: {} and SagaId: {} error: {}"
                    , orderId, sagaId, e.getMessage());
        }
    }
}

