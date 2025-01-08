package ec.com.sofka.aggregate.action;

import ec.com.sofka.account.Account;
import ec.com.sofka.aggregate.action.values.ActionId;
import ec.com.sofka.aggregate.events.TransactionRegistered;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.utils.AggregateRoot;
import ec.com.sofka.transaction.Transaction;
import ec.com.sofka.transaction.values.TransactionId;
import ec.com.sofka.utils.enums.TransactionTypes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Action extends AggregateRoot<ActionId> {
    private Transaction transaction;

    public Action() {
        super(new ActionId());
        setSubscription(new ActionHandler(this));
    }

    public Action(final String id) {
        super(ActionId.of(id));
        setSubscription(new ActionHandler(this));
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void registerTransaction(TransactionTypes transactionTypes, BigDecimal amount, BigDecimal fee, LocalDateTime date, String description, String accountId) {
        addEvent(new TransactionRegistered(new TransactionId().getValue(), transactionTypes, amount, fee, date, description, accountId)).apply();
    }

    public static Mono<Action> from(final String id, Flux<DomainEvent> events) {
        Action action = new Action(id);

        return events
                .filter(eventsFilter -> id.equals(eventsFilter.getAggregateRootId()))
                .flatMap(event -> Mono.fromRunnable(() -> action.addEvent(event).apply()))
                .then(Mono.just(action));
    }
}
