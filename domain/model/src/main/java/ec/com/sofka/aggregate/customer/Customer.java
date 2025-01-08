package ec.com.sofka.aggregate.customer;

import ec.com.sofka.account.Account;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.customer.values.CustomerId;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.utils.AggregateRoot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Customer extends AggregateRoot<CustomerId> {
    private Account account ;
    private List<Account> accounts = new ArrayList<>();

    public Customer() {
        super(new CustomerId());
        setSubscription(new CustomerHandler(this));
    }

    private Customer(final String id) {
        super(CustomerId.of(id));
        setSubscription(new CustomerHandler(this));
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void createAccount(BigDecimal accountBalance, String accountNumber, String accountHolder ) {
        addEvent(new AccountCreated(new AccountId().getValue(), accountNumber,accountBalance, accountHolder, new ArrayList<>())).apply();

    }

    public void updateAccountBalance(String id, BigDecimal balance, String accountNumber, String accountHolder) {
        addEvent(new AccountBalanceUpdated(id, accountNumber, balance, accountHolder, new ArrayList<>())).apply();
    }

    public static Mono<Customer> from(final String id, Flux<DomainEvent> events) {
        Customer customer = new Customer(id);
        return events
                .filter(eventsFilter -> id.equals(eventsFilter.getAggregateRootId()))
                .concatMap(event -> Mono.just(event)
                        .doOnNext(e -> customer.addEvent(e).apply())
                )
                .then(Mono.just(customer));
    }


}
