package com.bigos.restaurant.adapters.orderprocessed.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.restaurant.adapters.orderprocessed.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.restaurant.adapters.restaurant.exception.RestaurantApplicationException;
import com.bigos.restaurant.adapters.restaurant.exception.RestaurantNotFoundException;
import com.bigos.restaurant.domain.ports.in.message.OrderPaidEventListener;
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

@Slf4j
@Component
public class OrderPaidKafkaEventListener implements KafkaConsumer<OrderPaidEventDtoKafka> {

    private final OrderPaidEventListener orderPaidEventListener;
    private final InputMessagingKafkaDataMapper mapper;

    public OrderPaidKafkaEventListener(OrderPaidEventListener orderPaidEventListener, InputMessagingKafkaDataMapper mapper) {
        this.orderPaidEventListener = orderPaidEventListener;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(topics = "${restaurant-service.order-paid-events-topic-name}")
    public void receive(@Payload List<OrderPaidEventDtoKafka> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received OrderPaidEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(orderPaidKafkaEvent -> {
            String orderId = orderPaidKafkaEvent.getData().orderId();
            try {
                orderPaidEventListener.acceptOrder(mapper.orderPaidEventDtoKafkaToOrderPaidEvent(orderPaidKafkaEvent));

            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    log.error("Unique constraint exception with sql state: {} in OrderPaidKafkaEventListener for order id: {}"
                            , sqlException.getSQLState(), orderId);
                } else {
                    throw new RestaurantApplicationException("DataAccessException in OrderPaidKafkaEventListener: " + e.getMessage(), e);
                }
            } catch (RestaurantNotFoundException e) {
                log.error("Restaurant not found. Restaurant id: {}, order id: {}"
                        , orderPaidKafkaEvent.getData().restaurantId(), orderId);
            }
        });
    }
}
