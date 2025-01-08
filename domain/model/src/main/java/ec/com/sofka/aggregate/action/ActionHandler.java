package ec.com.sofka.aggregate.action;

import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.aggregate.events.TransactionRegistered;
import ec.com.sofka.generics.domain.DomainActionsContainer;
import ec.com.sofka.transaction.Transaction;
import ec.com.sofka.transaction.values.TransactionId;
import ec.com.sofka.transaction.values.objects.*;

public class ActionHandler  extends DomainActionsContainer {
    public ActionHandler (Action action) {

        addDomainActions((TransactionRegistered event) -> {
            Transaction transaction = new Transaction(
                    TransactionId.of(event.getId()),
                    TransactionType.of(event.getTransactionTypes()),
                    Amount.of(event.getAmount()),
                    Fee.of(event.getFee()),
                    Date.of(event.getDate()),
                    Description.of(event.getDescription()),
                    AccountId.of(event.getAccountId())
            );
            action.setTransaction(transaction);
        });
    }
}