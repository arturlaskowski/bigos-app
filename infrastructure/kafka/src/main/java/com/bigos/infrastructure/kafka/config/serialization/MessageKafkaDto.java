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
public class MessageKafkaDto<DTO extends Serializable> implements TypeDto, Serializable {

    private String dataId;

    private final String messageId = randomUUID().toString();

    private Instant createdAt;

    private String type;

    private DTO data;

    protected MessageKafkaDto(String dataId, Instant createdAt, DTO data) {
        this.dataId = dataId;
        setCreatedAt(createdAt);
        this.data = data;
        this.type = this.getClass().getName();
    }

    public Instant getCreatedAt() {
        return Instant.now();
    }

    public String getMessageId() {
        return messageId;
    }

    public String getDataId() {
        return dataId;
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

    public DTO getData() {
        return data;
    }
}
