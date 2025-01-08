package ec.com.sofka.appservice.transaction;

import ec.com.sofka.aggregate.action.Action;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.transaction.request.GetTransactionsByAccountNumberRequest;
import ec.com.sofka.appservice.transaction.responses.TransactionResponse;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

public class GetTransactionsByAccountNumberUseCase implements IUseCaseExecute<GetTransactionsByAccountNumberRequest, TransactionResponse> {

    private final IEventStore repository;

    public GetTransactionsByAccountNumberUseCase(IEventStore repository) {
        this.repository = repository;
    }

    @Override
    public Flux<TransactionResponse> execute(GetTransactionsByAccountNumberRequest cmd) {
        Flux<DomainEvent> eventsCustomer = repository.findAggregate(cmd.getCustomerId(), "customer");

        return Customer.from(cmd.getCustomerId(), eventsCustomer)
                .flatMapMany(customer -> Mono.justOrEmpty(
                                        customer.getAccounts().stream()
                                                .filter(account -> account.getAccountNumber().getValue().equals(cmd.getAccountNumber()))
                                                .findFirst()
                                )
                                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                                .flatMapMany(account -> {
                                    return repository.findAggregate(cmd.getAggregateId(), "action")
                                            .collectList()
                                            .flatMapMany(eventsAction -> {
                                                Map<String, DomainEvent> latestEvents = eventsAction.stream()
                                                        .collect(Collectors.toMap(
                                                                DomainEvent::getAggregateRootId,
                                                                event -> event,
                                                                (existing, replacement) -> existing.getVersion() >= replacement.getVersion() ? existing : replacement
                                                        ));

                                                return Flux.fromIterable(latestEvents.values())
                                                        .flatMap(event -> Action.from(event.getAggregateRootId(), Flux.fromIterable(eventsAction)));
                                            })
                                            .map(action -> new TransactionResponse(
                                                    action.getId().getValue(),
                                                    action.getTransaction().getAmount().getValue(),
                                                    action.getTransaction().getFee().getValue(),
                                                    action.getTransaction().getTransactionType().getValue(),
                                                    action.getTransaction().getDate().getValue(),
                                                    action.getTransaction().getAccountId().getValue(),
                                                    action.getTransaction().getDescription().getValue(),
                                                    customer.getId().getValue()
                                            ));
                                })
                );
    }
}