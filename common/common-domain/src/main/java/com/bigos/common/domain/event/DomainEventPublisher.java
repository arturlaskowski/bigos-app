package com.bigos.common.domain.event;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
