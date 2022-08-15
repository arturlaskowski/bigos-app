package com.bigos.order.adapters.in.message.kafka;

import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.order.adapters.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;
import com.bigos.order.domain.ports.in.message.PaymentStatusEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentStatusKafkaEventListener implements KafkaConsumer<PaymentStatusEventDtoKafka> {

    private final PaymentStatusEventListener paymentStatusEventListener;
    private final InputMessagingKafkaDataMapper mapper;

    public PaymentStatusKafkaEventListener(PaymentStatusEventListener paymentStatusEventListener, InputMessagingKafkaDataMapper mapper) {
        this.paymentStatusEventListener = paymentStatusEventListener;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(topics = "${order-service.payment-status-events-topic-name}")
    public void receive(@Payload List<PaymentStatusEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received PaymentStatusEvent {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentKafkaDto -> {
            PaymentStatusEvent paymentStatusEvent = mapper.paymentStatusEventDtoKafkaToPaymentStatusEvent(paymentKafkaDto);
            if (PaymentStatus.COMPLETED == paymentStatusEvent.paymentStatus()) {
                paymentStatusEventListener.paymentCompleted(paymentStatusEvent);

            } else if (PaymentStatus.CANCELLED == paymentStatusEvent.paymentStatus() ||
                    PaymentStatus.REJECTED == paymentStatusEvent.paymentStatus()) {
                paymentStatusEventListener.paymentCancelled(paymentStatusEvent);
            }
        });
    }
}