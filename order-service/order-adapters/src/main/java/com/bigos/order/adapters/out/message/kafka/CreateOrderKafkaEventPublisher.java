package com.bigos.order.adapters.out.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.OrderEventDtoKafka;
import com.bigos.order.adapters.config.OrderServiceConfigProperties;
import com.bigos.order.adapters.out.message.kafka.mapper.OrderMessagingDataMapper;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.ports.out.message.OrderCreatedEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaEventPublisher implements OrderCreatedEventPublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigProperties configProperties;
    private final KafkaPublisher<OrderEventDtoKafka> kafkaPublisher;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public CreateOrderKafkaEventPublisher(OrderMessagingDataMapper orderMessagingDataMapper, OrderServiceConfigProperties configProperties,
                                          KafkaPublisher<OrderEventDtoKafka> kafkaPublisher, KafkaCallbackHelper kafkaCallbackHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.configProperties = configProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().id().toString();
        try {
            OrderEventDtoKafka orderEventDto = orderMessagingDataMapper.orderCretedEventToOrderEventDto(domainEvent);
            kafkaPublisher.send(configProperties.getOrderCreatingEventsTopicName(),
                    orderId,
                    orderEventDto,
                    kafkaCallbackHelper
                            .getKafkaCallback(configProperties.getOrderCreatingEventsTopicName(),
                                    orderId,
                                    orderEventDto.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending OrderCreatedEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
