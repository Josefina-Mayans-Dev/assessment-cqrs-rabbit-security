package ec.com.sofka.appservice.account;



import ec.com.sofka.aggregate.customer.Customer;
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

    @Override
    public Flux<AccountResponse> get() {
        // Obtener todos los eventos de manera reactiva
        return eventRepository.findAllAggregates()
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

                                Log log = new Log("Getting all accounts", "account", null);
                                busMessage.sendMsg(log);

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
    }
}
