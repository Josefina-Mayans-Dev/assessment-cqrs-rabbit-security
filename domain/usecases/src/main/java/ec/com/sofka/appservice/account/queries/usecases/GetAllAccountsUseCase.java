package ec.com.sofka.appservice.account.queries.usecases;



import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.events.AccountUpdated;
import ec.com.sofka.appservice.account.queries.query.GetAccountQuery;
import ec.com.sofka.appservice.account.queries.responses.AccountResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import ec.com.sofka.generics.utils.QueryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllAccountsUseCase implements IUseCaseGet<GetAccountQuery, AccountResponse> {

    private final AccountRepository accountRepository;

    public GetAllAccountsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

    /*@Override
    public Mono<QueryResponse<AccountResponse>> get(GetAccountQuery query) {
        // Obtener todos los eventos de manera reactiva
        Flux<DomainEvent> events = eventRepository.findAllAggregates(); // Recuperar todos los eventos

        // Filtrar y mantener solo el último evento para cada Customer (en base a la versión)
        return events.collectList() // Recoger todos los eventos en una lista de manera reactiva
                .flatMap(eventsList -> {
                    // Filtrar solo los eventos más recientes para cada agregador (Customer)
                    Map<String, DomainEvent> maplatestEvents = eventsList.stream()
                            .filter(event -> event instanceof AccountCreated || event instanceof AccountUpdated || event instanceof AccountBalanceUpdated)
                            .collect(Collectors.toMap(
                                    DomainEvent::getAggregateRootId,   // Clave: aggregateId
                                    event -> event,                    // Valor: el evento
                                    (existing, replacement) -> existing.getVersion() >= replacement.getVersion() ? existing : replacement
                            ));

                    // Obtener solo los eventos más recientes
                    List<DomainEvent> latestEvents = maplatestEvents.values().stream().collect(Collectors.toList());

                    // Reconstruir los clientes de manera reactiva aplicando los eventos
                    return Flux.fromIterable(latestEvents)
                            .flatMap(event -> {
                                // Reconstruir cada cliente de manera reactiva usando los eventos filtrados
                                return Customer.from(event.getAggregateRootId(), Flux.fromIterable(latestEvents))
                                        .map(customer -> new AccountResponse(
                                                customer.getId().getValue(),
                                                customer.getAccount().getId().getValue(),
                                                customer.getAccount().getBalance().getValue(),
                                                customer.getAccount().getAccountNumber().getValue(),
                                                customer.getAccount().getAccountHolder().getValue()
                                        ));
                            })
                            .collectList() // Recoger todas las respuestas en una lista
                            .map(QueryResponse::ofMultiple); // Envolver la lista en un QueryResponse
                });
    }*/

    @Override
    public Mono<QueryResponse<AccountResponse>> get(GetAccountQuery query) {
        return accountRepository.findAll()
                .map(account ->
                        new AccountResponse(account.getId(), account.getBalance(), account.getAccountNumber(), account.getAccountHolder())
                )
                .collectList()
                .flatMap(accounts -> Mono.just(QueryResponse.ofMultiple(accounts)));
    }
}