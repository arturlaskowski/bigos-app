package com.bigos.common.domain.event.publisher;

import com.bigos.common.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent<T>> {

    void publish(T domainEvent);
}
