package ec.com.sofka.aggregate.events;

import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.transaction.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountUpdated extends DomainEvent {
    private String accountId;
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private List<Transaction> transactions;


    public AccountUpdated(String accountId,BigDecimal balance, String accountNumber, String accountHolder, List<Transaction> transactions) {
        super(EventsEnum.ACCOUNT_UPDATED.name());
        this.accountId = accountId;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.transactions = transactions;

    }

    public AccountUpdated() {
        super(EventsEnum.ACCOUNT_UPDATED.name());

    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getAccountId() {
        return accountId;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
