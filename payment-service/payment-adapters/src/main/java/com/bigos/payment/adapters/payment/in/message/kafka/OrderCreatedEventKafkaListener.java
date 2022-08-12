package com.bigos.payment.adapters.payment.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.model.OrderEventDtoKafka;
import com.bigos.infrastructure.kafka.model.OrderMessageDto;
import com.bigos.payment.adapters.payment.in.message.kafka.mapper.PaymentMessagingDataMapper;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bigos.common.domain.vo.OrderStatus.CANCELLING;
import static com.bigos.common.domain.vo.OrderStatus.PENDING;

@Slf4j
@Component
public class OrderCreatedEventKafkaListener implements KafkaConsumer< OrderEventDtoKafka> {

    private final PaymentMessageListener paymentMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public OrderCreatedEventKafkaListener(PaymentMessageListener paymentMessageListener, PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-config.consumer.payment-consumer-group-id}",
            topics = "${payment-service.order-creating-events-topic-name}")
    public void receive(@Payload List<OrderEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("recived {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(orderEventDto -> {
            if (PENDING.name().equals(orderEventDto.getData().status())) {
                log.info("Processing payment for order id: {}", orderEventDto.getDataId());
                paymentMessageListener.makePayment(paymentMessagingDataMapper
                        .orderEventDtoToMakePaymentCommand(orderEventDto));

            } else if (CANCELLING.name().equals(orderEventDto.getData().status())) {
                log.info("Cancelling payment for order id: {}", orderEventDto.getDataId());
                paymentMessageListener.cancelPayment(paymentMessagingDataMapper
                        .orderEventDtoToCancelPaymentCommand(orderEventDto));
            }
        });
    }
}
