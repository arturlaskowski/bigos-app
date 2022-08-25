package com.bigos.infrastructure.kafka.config.producer;

import com.bigos.common.domain.outbox.AbstractOutboxMessage;
import com.bigos.common.domain.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.util.function.Consumer;

@Slf4j
@Component
public class KafkaOutboxCallbackHelper {

    public <T, U extends AbstractOutboxMessage> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(String topiName, String itemId, String messageDtoName, U outboxMessage, Consumer<U> outboxCallback) {
        return new ListenableFutureCallback<SendResult<String, T>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending {} itemId {} to topic {}", messageDtoName, itemId, topiName, ex);

                outboxMessage.setOutboxStatus(OutboxStatus.FAILED);
                outboxCallback.accept(outboxMessage);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Send to kafka {} itemId {} to topic {} Partition: {} Offset: {} Timestamp: {}",
                        messageDtoName, itemId, metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());

                outboxMessage.setSendDate(Instant.now());
                outboxMessage.setOutboxStatus(OutboxStatus.COMPLETED);
                outboxCallback.accept(outboxMessage);
            }
        };
    }
}
