package com.bigos.payment.adapters.payment.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.payment.adapters.payment.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bigos.common.domain.vo.OrderStatus.CANCELLING;

@Slf4j
@Component
public class OrderCancellingEventKafkaListener implements KafkaConsumer<OrderCancellingEventDtoKafka> {

    private final PaymentMessageListener paymentMessageListener;
    private final InputMessagingKafkaDataMapper inputMessagingKafkaDataMapper;

    public OrderCancellingEventKafkaListener(PaymentMessageListener paymentMessageListener, InputMessagingKafkaDataMapper inputMessagingKafkaDataMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.inputMessagingKafkaDataMapper = inputMessagingKafkaDataMapper;
    }

    @Override
    @KafkaListener(topics = "${payment-service.order-cancelling-events-topic-name}")
    public void receive(@Payload List<OrderCancellingEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received OrderCancellingEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(orderCancellingEvent -> {
            if (CANCELLING.name().equals(orderCancellingEvent.getData().status())) {
                log.info("Cancelling payment for order id: {}", orderCancellingEvent.getDataId());
                paymentMessageListener.cancelPayment(inputMessagingKafkaDataMapper
                        .orderEventDtoToCancelPaymentCommand(orderCancellingEvent));
            }
        });
    }
}
