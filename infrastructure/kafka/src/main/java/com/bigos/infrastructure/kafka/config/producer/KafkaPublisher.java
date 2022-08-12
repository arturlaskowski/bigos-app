package com.bigos.infrastructure.kafka.config.producer;

import com.bigos.infrastructure.kafka.config.producer.exception.KafkaProducerException;
import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;

@Slf4j
@Component
public class KafkaPublisher<V extends MessageKafkaDto> {

    private final KafkaTemplate<String, V> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topicName, String key, V message, ListenableFutureCallback<SendResult<String, V>> callback) {
        log.info("Sending message={} to topic={}", message, topicName);
        try {
            ListenableFuture<SendResult<String, V>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
            kafkaResultFuture.addCallback(callback);
        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message,
                    e.getMessage());
            throw new KafkaProducerException("Error on kafka producer with key: " + key + " and message: " + message);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }
}
