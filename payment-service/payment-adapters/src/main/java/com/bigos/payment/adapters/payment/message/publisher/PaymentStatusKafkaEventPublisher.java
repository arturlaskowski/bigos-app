package com.bigos.payment.adapters.payment.message.publisher;

import com.bigos.infrastructure.kafka.config.producer.KafkaOutboxCallbackHelper;
import com.bigos.infrastructure.kafka.config.producer.KafkaPublisher;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.payment.application.payment.config.PaymentServiceConfigProperties;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMapper;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMessage;
import com.bigos.payment.application.payment.outbox.publisher.PaymentStatusMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class PaymentStatusKafkaEventPublisher implements PaymentStatusMessagePublisher {

    private final PaymentOutboxMapper mapper;
    private final KafkaPublisher<PaymentStatusEventDtoKafka> kafkaPublisher;
    private final PaymentServiceConfigProperties properties;
    private final KafkaOutboxCallbackHelper kafkaCallbackHelper;

    public PaymentStatusKafkaEventPublisher(PaymentOutboxMapper mapper,
                                            KafkaPublisher<PaymentStatusEventDtoKafka> kafkaPublisher,
                                            PaymentServiceConfigProperties properties,
                                            KafkaOutboxCallbackHelper kafkaCallbackHelper) {
        this.mapper = mapper;
        this.kafkaPublisher = kafkaPublisher;
        this.properties = properties;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(PaymentOutboxMessage paymentOutboxMessage, Consumer<PaymentOutboxMessage> outboxCallback) {
        String sagaId = paymentOutboxMessage.getSagaId().toString();

        try {
            PaymentStatusEventDtoKafka paymentStatusEventDtoKafka = mapper.paymentOutboxMessageToPaymentStatusEventDtoKafka(paymentOutboxMessage);

            kafkaPublisher.send(properties.getPaymentStatusEventsTopicName(), sagaId, paymentStatusEventDtoKafka,
                    kafkaCallbackHelper.getKafkaCallback
                            (properties.getPaymentStatusEventsTopicName(), paymentOutboxMessage.getAggregateId().toString(),
                                    paymentStatusEventDtoKafka.getClass().getSimpleName(), paymentOutboxMessage, outboxCallback));

        } catch (Exception e) {
            log.error("Error while sending PaymentStatusEvent message to kafka. Saga id: {}, error: {}", sagaId, e.getMessage());
        }
    }
}
