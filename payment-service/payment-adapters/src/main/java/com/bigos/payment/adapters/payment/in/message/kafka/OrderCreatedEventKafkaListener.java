package com.bigos.payment.adapters.payment.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.OrderCreatedEventDtoKafka;
import com.bigos.payment.adapters.payment.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bigos.common.domain.vo.OrderStatus.PENDING;

@Slf4j
@Component
public class OrderCreatedEventKafkaListener implements KafkaConsumer<OrderCreatedEventDtoKafka> {

    private final PaymentMessageListener paymentMessageListener;
    private final InputMessagingKafkaDataMapper inputMessagingKafkaDataMapper;

    public OrderCreatedEventKafkaListener(PaymentMessageListener paymentMessageListener, InputMessagingKafkaDataMapper inputMessagingKafkaDataMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.inputMessagingKafkaDataMapper = inputMessagingKafkaDataMapper;
    }

    @Override
    @KafkaListener(topics = "${payment-service.order-created-events-topic-name}")
    public void receive(@Payload List<OrderCreatedEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received OrderCreatedEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(orderEventDto -> {
            if (PENDING.name().equals(orderEventDto.getData().status())) {
                log.info("Processing payment for order id: {}", orderEventDto.getDataId());
                paymentMessageListener.makePayment(inputMessagingKafkaDataMapper
                        .orderEventDtoToMakePaymentCommand(orderEventDto));
            }
        });
    }
}
