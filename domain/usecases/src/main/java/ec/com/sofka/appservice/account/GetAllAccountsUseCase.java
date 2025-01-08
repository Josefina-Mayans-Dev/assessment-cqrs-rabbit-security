package ec.com.sofka.appservice.account;


/*
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.account.responses.AccountResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.BusMessage;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        return eventRepository.findAllAggregates()
                .collectList() // Agrupar todos los eventos en una lista
                .flatMapMany(events -> {
                            // Procesar los últimos eventos
                            Map<String, List<DomainEvent>> eventsByAggregate = events.stream()
                                    .collect(Collectors.groupingBy(DomainEvent::getAggregateRootId));

                            // Reconstruir los clientes de manera reactiva
                            return Flux.fromIterable(eventsByAggregate.entrySet())
                                    .flatMap(entry -> Mono.fromCallable(() ->
                                            Customer.from(entry.getKey(), entry.getValue()) // Usar el método `from` sin modificar
                                    ))
                .map(customer -> new AccountResponse(
                        customer.getId().getValue(),
                        customer.getAccount().getId().getValue(),
                        customer.getAccount().getNumber().getValue(),
                        customer.getAccount().getName().getValue(),
                        customer.getAccount().getBalance().getValue()
                ));
    });


    */
/*@Override
    public Flux<AccountResponse> get(){
        Log log = new Log("Getting all accounts", "account", null);
        busMessage.sendMsg(log);
        return accountRepository.findAll();
    }*//*

}
}*/
