package com.bigos.common.domain.event.publisher;

import com.bigos.common.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
