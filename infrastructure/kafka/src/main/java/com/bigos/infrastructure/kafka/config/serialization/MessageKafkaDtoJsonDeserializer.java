package com.bigos.infrastructure.kafka.config.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class MessageKafkaDtoJsonDeserializer implements Deserializer<TypeDto> {

    private JsonDeserializer<MessageKafkaDto> messageKafkaJsonDeserializer;

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        messageKafkaDtoDeserializer().configure(configs, isKey);
    }

    @Override
    public TypeDto deserialize(String topic, byte[] data) {
        if (data == null) {
            log.error("Received empty message to deserializing");
            return null;
        }
        try {
            MessageKafkaDto item = messageKafkaDtoDeserializer().deserialize(topic, data);
            Class<?> clz = Class.forName(item.getType());
            return objectMapper.readerFor(clz)
                    .readValue(data);
        } catch (Exception e) {
            throw new SerializationException("Cannot deserialize kafka message to MessageKafkaDto. Message: " + new String(data));
        }
    }

    @Override
    public void close() {
        messageKafkaDtoDeserializer().close();
    }

    private JsonDeserializer<MessageKafkaDto> messageKafkaDtoDeserializer() {
        if (messageKafkaJsonDeserializer == null) {
            messageKafkaJsonDeserializer = new JsonDeserializer<>(MessageKafkaDto.class, objectMapper);
        }
        return messageKafkaJsonDeserializer;
    }
}
