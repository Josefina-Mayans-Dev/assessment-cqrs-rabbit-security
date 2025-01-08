package ec.com.sofka.transaction;

import ec.com.sofka.account.Account;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.generics.utils.Entity;
import ec.com.sofka.transaction.values.TransactionId;
import ec.com.sofka.transaction.values.objects.*;

public class Transaction extends Entity<TransactionId> {
    private TransactionType transactionType;
    private Amount amount;
    private Fee fee;
    private Date date;
    private Description description;
    private AccountId accountId;

    public Transaction(TransactionId id, TransactionType transactionType, Amount amount, Fee fee, Date date, Description description, AccountId accountId) {
        super(id);
        this.transactionType = transactionType;
        this.amount = amount;
        this.fee = fee;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Amount getAmount() {
        return amount;
    }

    public Fee getFee() {
        return fee;
    }

    public Date getDate() {
        return date;
    }

    public Description getDescription() {
        return description;
    }

    public AccountId getAccountId() {
        return accountId;
    }

}
