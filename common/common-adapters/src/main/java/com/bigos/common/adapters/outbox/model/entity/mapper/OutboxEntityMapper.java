package com.bigos.common.adapters.outbox.model.entity.mapper;

import com.bigos.common.adapters.outbox.exception.OutboxException;
import com.bigos.common.adapters.outbox.model.entity.OutboxEntity;
import com.bigos.common.domain.outbox.AbstractOutboxMessage;
import com.bigos.common.domain.outbox.OutboxPayload;
import org.springframework.beans.factory.annotation.Autowired;

public class OutboxEntityMapper<E extends OutboxEntity> {

    @Autowired
    private OutboxPayloadHelper outboxPayloadHelper;

    public <P extends OutboxPayload, G extends AbstractOutboxMessage> G outboxEntityToOutboxMessage(E outboxEntity, Class<G> outboxMessageClass) {
        G outboxMessage = null;
        Class<P> payloadClass = null;
        try {
            outboxMessage = outboxMessageClass.getDeclaredConstructor().newInstance();
            payloadClass = (Class<P>) Class.forName(outboxEntity.getPayloadType());
        } catch (Exception e) {
            throw new OutboxException("Could not map " + outboxEntity.getClass().getName() + "  to " + outboxMessageClass.getName(), e);
        }

        outboxMessage.setId(outboxEntity.getId());
        outboxMessage.setSagaId(outboxEntity.getSagaId());
        outboxMessage.setCreatedDate(outboxEntity.getCreatedDate());
        outboxMessage.setSendDate(outboxEntity.getSendDate());
        outboxMessage.setAggregateId(outboxEntity.getAggregateId());
        outboxMessage.setAggregateName(outboxEntity.getAggregateName());
        outboxMessage.setOutboxStatus(outboxEntity.getOutboxStatus());
        outboxMessage.setVersion(outboxEntity.getVersion());
        outboxMessage.setPayload(outboxPayloadHelper.getMessagePayloadFromJsonNode(outboxEntity.getPayload(), payloadClass));
        return outboxMessage;
    }


    public <G extends AbstractOutboxMessage> E outboxMessageToOutboxEntity(G outboxMessage, Class<E> entityOutboxClass) {
        E entity = null;
        try {
            entity = entityOutboxClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OutboxException("Could not map " + entityOutboxClass.getName() + "  to " + outboxMessage.getClass().getName(), e);
        }

        entity.setId(outboxMessage.getId());
        entity.setSagaId(outboxMessage.getSagaId());
        entity.setCreatedDate(outboxMessage.getCreatedDate());
        entity.setSendDate(outboxMessage.getSendDate());
        entity.setAggregateId(outboxMessage.getAggregateId());
        entity.setAggregateName(outboxMessage.getAggregateName());
        entity.setOutboxStatus(outboxMessage.getOutboxStatus());
        entity.setPayload(outboxPayloadHelper.convertMessagePayloadToJsonNode(outboxMessage.getPayload()));
        entity.setPayloadType(outboxMessage.getPayload().getClass().getName());
        entity.setVersion(outboxMessage.getVersion());

        return entity;
    }
}
