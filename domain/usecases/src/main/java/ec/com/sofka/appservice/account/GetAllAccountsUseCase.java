package ec.com.sofka.appservice.account;



import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.events.AccountUpdated;
import ec.com.sofka.appservice.account.responses.AccountResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.BusMessage;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import ec.com.sofka.log.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllAccountsUseCase implements IUseCaseGet<AccountResponse> {
    private final AccountRepository accountRepository;
    private final IEventStore eventRepository;
    private final BusMessage busMessage;

    public GetAllAccountsUseCase(AccountRepository accountRepository, IEventStore eventRepository, BusMessage busMessage) {
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
        this.busMessage = busMessage;
    }

  /* @Override
    public Flux<AccountResponse> get() {
        // Obtener todos los eventos de manera reactiva
        return eventRepository.findAllAggregates()
                .filter(event ->
                        event instanceof AccountCreated || event instanceof AccountUpdated || event instanceof AccountBalanceUpdated)
                .collectList() // Recoger todos los eventos en una lista de manera reactiva
                .flatMapMany(events -> {
                    // Filtrar los eventos más recientes por agregados
                    Map<String, DomainEvent> latestEventsMap = events.stream()
                            .collect(Collectors.toMap(
                                    DomainEvent::getAggregateRootId,   // Clave: aggregateId
                                    event -> event,                    // Valor: el evento
                                    (existing, replacement) -> existing.getVersion() >= replacement.getVersion() ? existing : replacement
                            ));

                    // Obtener los eventos más recientes de manera reactiva (convertir a Flux)
                    Flux<DomainEvent> fluxEvents = Flux.fromIterable(latestEventsMap.values()); // Convertir la lista a Flux

                    // Obtener los eventos más recientes de manera reactiva (convertir a Flux)
                    return Flux.fromIterable(latestEventsMap.values()) // Convirtiendo los eventos a un Flux
                            .flatMap(event -> {
                                // Reconstruir el cliente de manera reactiva
                                Mono<Customer> customerMono = Customer.from(event.getAggregateRootId(), fluxEvents);

                               *//* Log log = new Log("Getting all accounts", "account", null);
                                busMessage.sendMsg(log);*//*

                                return customerMono.map(customer -> new AccountResponse(
                                        customer.getId().getValue(),
                                        customer.getAccount().getId().getValue(),
                                        customer.getAccount().getBalance().getValue(),
                                        customer.getAccount().getAccountNumber().getValue(),
                                        customer.getAccount().getAccountHolder().getValue(),
                                        customer.getAccount().getTransactions().getValue()
                                ));
                            });
                });
    }*/

    @Override
    public Flux<AccountResponse> get() {
        // Obtener todos los eventos de manera reactiva
        return eventRepository.findAllAggregates()
                .filter(event ->
                        event instanceof AccountCreated ||
                                event instanceof AccountUpdated ||
                                event instanceof AccountBalanceUpdated) // Filtrar solo eventos de tipo cuenta
                .collectList() // Recoger todos los eventos de cuenta en una lista de manera reactiva
                .flatMapMany(events -> {
                    // Filtrar los eventos más recientes por agregados, pero ahora tenemos todos los eventos para cada cuenta
                    Map<String, List<DomainEvent>> eventsByAggregate = events.stream()
                            .collect(Collectors.groupingBy(DomainEvent::getAggregateRootId));  // Agrupar eventos por accountId

                    // Procesar los eventos de cada cuenta
                    return Flux.fromIterable(eventsByAggregate.entrySet()) // Convertir el mapa en un Flux de entradas
                            .flatMap(entry -> {
                                // Aplicar los eventos de la cuenta en el orden correcto (por versión)
                                List<DomainEvent> sortedEvents = entry.getValue().stream()
                                        .sorted(Comparator.comparing(DomainEvent::getVersion))
                                        .collect(Collectors.toList());

                                // Reconstruir el cliente de manera reactiva aplicando los eventos
                                Mono<Customer> customerMono = Customer.from(entry.getKey(), Flux.fromIterable(sortedEvents));

                                return customerMono.map(customer -> new AccountResponse(
                                        customer.getId().getValue(),
                                        customer.getAccount().getId().getValue(),
                                        customer.getAccount().getBalance().getValue(), // Aquí ya tendrá el balance actualizado
                                        customer.getAccount().getAccountNumber().getValue(),
                                        customer.getAccount().getAccountHolder().getValue(),
                                        customer.getAccount().getTransactions().getValue()
                                ));
                            });
                });
    }
}