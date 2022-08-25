package com.bigos.common.adapters.outbox.model.entity.mapper;

import com.bigos.common.domain.outbox.OutboxPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class OutboxPayloadHelper {

    private final ObjectMapper objectMapper;

    OutboxPayloadHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    <T extends OutboxPayload> T getMessagePayloadFromJsonNode(JsonNode payload, Class<T> outputType) {
        return objectMapper.convertValue(payload, outputType);
    }

    <T extends OutboxPayload> JsonNode convertMessagePayloadToJsonNode(T payload) {
        return objectMapper.convertValue(payload, JsonNode.class);
    }
}
