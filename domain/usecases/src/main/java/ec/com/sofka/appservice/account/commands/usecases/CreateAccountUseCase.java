package ec.com.sofka.appservice.account.commands.usecases;


import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.account.commands.CreateAccountCommand;
import ec.com.sofka.appservice.account.queries.responses.AccountResponse;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CreateAccountUseCase implements IUseCaseExecute<CreateAccountCommand, AccountResponse> {
    private final IEventStore repository;
    private final BusEvent busEvent;

    public CreateAccountUseCase(IEventStore repository, BusEvent busEvent) {
        this.repository = repository;
        this.busEvent = busEvent;
    }


    @Override
    public Mono<AccountResponse> execute(CreateAccountCommand cmd) {
        Flux<DomainEvent> events = repository.findAggregate(cmd.getAggregateId(), "customer");

        Customer customer = new Customer();

        Customer.from(cmd.getAggregateId(), events);

        customer.createAccount(cmd.getBalance(), cmd.getNumber(), cmd.getCustomerName());

        customer.getUncommittedEvents()
                            .stream()
                            .map(repository::save)
                            .forEach(busEvent::sendEventAccountCreated);

        customer.markEventsAsCommitted();

                       return Mono.just(new AccountResponse(
                                customer.getId().getValue(),
                                customer.getAccount().getId().getValue(),
                                customer.getAccount().getBalance().getValue(),
                                customer.getAccount().getAccountNumber().getValue(),
                                customer.getAccount().getAccountHolder().getValue(),
                                customer.getAccount().getTransactions().getValue()
                        ));
    }
}
