package com.bigos.infrastructure.kafka.config.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KafkaCallbackHelper {

    public <T> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(String topiName, String itemId, String messageDtoName) {
        return new ListenableFutureCallback<SendResult<String, T>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending {} itemId {} to topic {}", messageDtoName, itemId, topiName, ex);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Send to kafka {} itemId {} to topic {} Partition: {} Offset: {} Timestamp: {}",
                        messageDtoName, itemId, metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
            }
        };
    }
}
