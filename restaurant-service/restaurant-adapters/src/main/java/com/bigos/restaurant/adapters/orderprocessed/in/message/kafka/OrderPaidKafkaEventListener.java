package com.bigos.restaurant.adapters.orderprocessed.in.message.kafka;

import com.bigos.infrastructure.kafka.config.cnosumer.KafkaConsumer;
import com.bigos.infrastructure.kafka.model.events.OrderPaidEventDtoKafka;
import com.bigos.restaurant.adapters.orderprocessed.in.message.kafka.mapper.InputMessagingKafkaDataMapper;
import com.bigos.restaurant.domain.ports.in.message.OrderPaidListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderPaidKafkaEventListener implements KafkaConsumer<OrderPaidEventDtoKafka> {

    private final OrderPaidListener orderPaidListener;
    private final InputMessagingKafkaDataMapper mapper;

    public OrderPaidKafkaEventListener(OrderPaidListener orderPaidListener, InputMessagingKafkaDataMapper mapper) {
        this.orderPaidListener = orderPaidListener;
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
            orderPaidListener.acceptOrder(mapper.orderPaidEventDtoKafkaToOrderPaidEvent(orderPaidKafkaEvent));
        });
    }
}
