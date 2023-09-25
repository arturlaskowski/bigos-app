package com.bigos.infrastructure.kafka.config.cnosumer;

import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;

import java.util.List;

public interface KafkaConsumer<M extends MessageKafkaDto> {

    void receive(List<M> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
