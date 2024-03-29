package com.bigos.infrastructure.kafka.config.serialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

import static java.util.UUID.randomUUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageKafkaDto<T extends Serializable> implements TypeDto, Serializable {

    private String dataId;

    private final String messageId = randomUUID().toString();

    private Instant createdAt;

    private String type;

    private T data;

    private String sagaId;

    protected MessageKafkaDto(String dataId, Instant createdAt, T data, String sageId) {
        this.dataId = dataId;
        setCreatedAt(createdAt);
        this.data = data;
        this.sagaId = sageId;
        this.type = this.getClass().getName();
    }

    public Instant getCreatedAt() {
        return Instant.now();
    }

    @Override
    public String getType() {
        return type;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        this.createdAt = createdAt;
    }

}
