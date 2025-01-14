package ec.com.sofka.gateway;

import ec.com.sofka.generics.domain.DomainEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//Here you can create more functions according to the needs of the project related to events
public interface IEventStore {
    Mono<DomainEvent> save(DomainEvent event);
    Flux<DomainEvent> findAggregate(String aggregateId, String aggregate);
    Flux<DomainEvent> findAllAggregatesBy(String aggregate);
    Flux<DomainEvent> findAllAggregates();
    Flux<DomainEvent> findAggregateforUpdate(String aggregateId);
}
