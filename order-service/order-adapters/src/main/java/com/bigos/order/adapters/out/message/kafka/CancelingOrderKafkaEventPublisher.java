package com.bigos.order.adapters.out.message.kafka;


import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.order.adapters.config.OrderServiceConfigProperties;
import com.bigos.order.adapters.out.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.order.domain.event.OrderCancellingEvent;
import com.bigos.order.domain.ports.out.message.OrderCancellingEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelingOrderKafkaEventPublisher implements OrderCancellingEventPublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final OrderServiceConfigProperties properties;
    private final KafkaPublisher<OrderCancellingEventDtoKafka> kafkaPublisher;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public CancelingOrderKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                             OrderServiceConfigProperties orderServiceConfigProperties,
                                             KafkaPublisher<OrderCancellingEventDtoKafka> kafkaPublisher,
                                             KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.properties = orderServiceConfigProperties;
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderCancellingEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().id().toString();

        try {
            OrderCancellingEventDtoKafka orderCancellingEventDtoKafka = mapper.orderCancellingEventToOrderCancellingEventDtoKafka(domainEvent);

            kafkaPublisher.send(properties.getOrderCancellingEventsTopicName(), orderId, orderCancellingEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getOrderCancellingEventsTopicName(), orderId, orderCancellingEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending OrderCancellingEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}

