package com.bigos.infrastructure.kafka.config.serialization;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageKafkaDtoJsonSerializer<T extends MessageKafkaDto> extends JsonSerializer<T> {

    @Override
    public byte[] serialize(String topic, MessageKafkaDto data) {
        try {
            if (data == null) {
                log.error("Empty message to serializing");
                return new byte[0];
            }

            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageKafkaDto to byte[]");
        }
    }
}