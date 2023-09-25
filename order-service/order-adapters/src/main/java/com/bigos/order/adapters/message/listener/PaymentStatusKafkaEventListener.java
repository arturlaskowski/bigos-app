package com.bigos.order.adapters.message.listener;

import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.order.application.exception.OrderNotFoundException;
import com.bigos.order.application.saga.OrderPaymentSaga;
import com.bigos.order.domain.event.PaymentStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentStatusKafkaEventListener implements KafkaConsumer<PaymentStatusEventDtoKafka> {

    private final OrderPaymentSaga orderPaymentSaga;
    private final InputMessagingKafkaDataMapper mapper;

    public PaymentStatusKafkaEventListener(OrderPaymentSaga orderPaymentSaga, InputMessagingKafkaDataMapper mapper) {
        this.orderPaymentSaga = orderPaymentSaga;
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
            String orderId = paymentKafkaDto.getData().orderId();
            try {
                PaymentStatusEvent paymentStatusEvent = mapper.paymentStatusEventDtoKafkaToPaymentStatusEvent(paymentKafkaDto);
                if (PaymentStatus.COMPLETED == paymentStatusEvent.paymentStatus()) {
                    orderPaymentSaga.process(paymentStatusEvent);
                    log.info("Order with id: {} is paid", paymentStatusEvent.orderId());

                } else if (PaymentStatus.CANCELLED == paymentStatusEvent.paymentStatus() ||
                        PaymentStatus.REJECTED == paymentStatusEvent.paymentStatus()) {
                    orderPaymentSaga.rollback(paymentStatusEvent);
                    log.info("Order with id: {} is cancelled, failure messages: {}"
                            , paymentStatusEvent.orderId(), paymentStatusEvent.failureMessages());
                }
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking exception in PaymentStatusKafkaEventListener for order id: {}", orderId);
            } catch (OrderNotFoundException e) {
                log.error("Order not found, order id: {}", orderId);
            }
        });
    }
}
