package com.bigos.payment.adapters.payment.out.repository.message.kafka;

import com.bigos.infrastructure.kafka.config.producer.KafkaCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.payment.adapters.payment.config.PaymentServiceConfigProperties;
import com.bigos.payment.adapters.payment.out.repository.message.kafka.mapper.OutputMessagingKafkaDataMapper;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.ports.out.message.PaymentRejectedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentRejectedKafkaEventPublisher implements PaymentRejectedMessagePublisher {

    private final OutputMessagingKafkaDataMapper mapper;
    private final KafkaPublisher<PaymentStatusEventDtoKafka> kafkaPublisher;
    private final PaymentServiceConfigProperties properties;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    public PaymentRejectedKafkaEventPublisher(OutputMessagingKafkaDataMapper outputMessagingKafkaDataMapper,
                                              KafkaPublisher<PaymentStatusEventDtoKafka> kafkaPublisher,
                                              PaymentServiceConfigProperties paymentServiceConfigProperties,
                                              KafkaCallbackHelper kafkaCallbackHelper) {
        this.mapper = outputMessagingKafkaDataMapper;
        this.kafkaPublisher = kafkaPublisher;
        this.properties = paymentServiceConfigProperties;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(PaymentRejectedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().id().toString();

        try {
            PaymentStatusEventDtoKafka paymentStatusEventDtoKafka = mapper.paymentRejectedEventToPaymentStatusEventDtoKafka(domainEvent);

            kafkaPublisher.send(properties.getPaymentStatusEventsTopicName(),
                    orderId,
                    paymentStatusEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback(properties.getPaymentStatusEventsTopicName()
                            , orderId, paymentStatusEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending PaymentStatusEvent message to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}