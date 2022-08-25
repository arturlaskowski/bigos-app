package com.bigos.payment.adapters.payment.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.OrderCancellingEventDtoKafka;
import com.bigos.payment.adapters.payment.exception.PaymentApplicationException;
import com.bigos.payment.adapters.payment.exception.PaymentNotFoundException;
import com.bigos.payment.adapters.payment.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
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
            String orderId = orderCancellingEvent.getDataId();

            try {
                if (CANCELLING.name().equals(orderCancellingEvent.getData().status())) {
                    paymentMessageListener.cancelPayment(inputMessagingKafkaDataMapper
                            .orderEventDtoToCancelPaymentCommand(orderCancellingEvent));
                }

            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    log.error("Unique constraint exception with sql state: {} " +
                            "in OrderCreatedEventKafkaListener for order id: {}", sqlException.getSQLState(), orderId);
                } else {
                    throw new PaymentApplicationException("DataAccessException in OrderCreatedEventKafkaListener: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                log.error("No found payment with order id: {}", orderId);
            }
        });
    }
}
