package com.bigos.order.adapters.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.order.adapters.config.OrderServiceConfigProperties;
import com.bigos.order.adapters.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.order.domain.event.OrderPaidEvent;
import com.bigos.order.domain.ports.out.message.OrderPaidEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPaidKafkaEventPublisher implements OrderPaidEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderPaidEventDtoKafka> kafkaPublisher;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public OrderPaidKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                        OrderServiceConfigProperties orderServiceConfigProperties,
                                        KafkaPublisher<OrderPaidEventDtoKafka> kafkaPublisher,
                                        KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.properties = orderServiceConfigProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().id().toString();

        try {
            OrderPaidEventDtoKafka orderPaidEventDto = mapper.orderPaidEventToOrderPaidEventDtoKafka(domainEvent);

            kafkaPublisher.send(properties.getOrderPaidEventsTopicName(), orderId, orderPaidEventDto,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderPaidEventsTopicName(), orderId, orderPaidEventDto.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending OrderPaidEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}

