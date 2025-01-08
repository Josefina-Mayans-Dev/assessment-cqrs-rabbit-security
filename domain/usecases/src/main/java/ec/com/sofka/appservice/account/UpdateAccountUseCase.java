package ec.com.sofka.appservice.account;

import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.account.request.UpdateAccountRequest;
import ec.com.sofka.appservice.account.responses.AccountResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class UpdateAccountUseCase implements IUseCaseExecute<UpdateAccountRequest, AccountResponse> {
    private final AccountRepository accountRepository;
    private final IEventStore eventRepository;

    public UpdateAccountUseCase(AccountRepository accountRepository, IEventStore eventRepository) {
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Mono<AccountResponse> execute(UpdateAccountRequest request) {
        return eventRepository.findAggregate(request.getAggregateId(), "customer")
                .collectList()
                .flatMap(events -> {
                    Mono<Customer> customerMono = Customer.from(request.getAggregateId(), Flux.fromIterable(events));

                    return customerMono.flatMap(customer -> {
                        customer.updateAccount(
                                customer.getAccount().getId().getValue(),
                                request.getBalance(),
                                request.getAccountNumber(),
                                request.getAccountHolder()
                        );

                        Mono<AccountDTO> updateResultMono = accountRepository.update(
                                new AccountDTO(customer.getAccount().getId().getValue(),
                                        request.getAccountNumber(),
                                        customer.getAccount().getBalance().getValue(),
                                        request.getAccountHolder()
                                ));

                        return updateResultMono.flatMap(result -> {
                            Flux.fromIterable(customer.getUncommittedEvents())
                                    .doOnNext(eventRepository::save)
                                    .subscribe(); // No queremos bloquear la ejecución principal, así que usamos `subscribe`


                            customer.markEventsAsCommitted();

                            return Mono.just(new AccountResponse(
                                    request.getAggregateId(),
                                    result.getId(),
                                    result.getBalance(),
                                    result.getAccountNumber(),
                                    result.getAccountHolder()
                            ));
                        });
                    });
                });

    }
}