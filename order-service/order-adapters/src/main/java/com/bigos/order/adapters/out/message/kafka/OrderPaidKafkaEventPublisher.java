package com.bigos.order.adapters.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaOutboxCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.order.adapters.config.OrderServiceConfigProperties;
import com.bigos.order.adapters.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.order.domain.ports.dto.outbox.OrderPaidOutboxMessage;
import com.bigos.order.domain.ports.out.message.OrderPaidEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class OrderPaidKafkaEventPublisher implements OrderPaidEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderPaidEventDtoKafka> kafkaPublisher;
    private final KafkaOutboxCallbackHelper kafkaCallbackHelper;

    public OrderPaidKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                        OrderServiceConfigProperties orderServiceConfigProperties,
                                        KafkaPublisher<OrderPaidEventDtoKafka> kafkaPublisher,
                                        KafkaOutboxCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.properties = orderServiceConfigProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderPaidOutboxMessage outboxMessage, Consumer<OrderPaidOutboxMessage> outboxCallback) {
        String orderId = outboxMessage.getAggregateId().toString();
        String sagaId = outboxMessage.getSagaId().toString();
        try {
            OrderPaidEventDtoKafka orderPaidEventDtoKafka = mapper.orderPaidOutboxMessageToOrderPaidEventDtoKafka(outboxMessage);

            kafkaPublisher.send(properties.getOrderPaidEventsTopicName(), sagaId, orderPaidEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderPaidEventsTopicName(), orderId, orderPaidEventDtoKafka.getClass().getSimpleName(),
                                    outboxMessage, outboxCallback));

        } catch (Exception e) {
            log.error("Error while sending OrderPaidEvent message to kafka. Order id: {} and SagaId: {} error: {}"
                    , orderId, sagaId, e.getMessage());
        }
    }
}

