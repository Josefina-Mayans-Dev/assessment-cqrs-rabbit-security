package ec.com.sofka.aggregate.events;

import ec.com.sofka.account.values.objects.Transactions;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AccountBalanceUpdated extends DomainEvent {
    private String id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountHolder;
    private List<Transaction> transactions;

    public AccountBalanceUpdated(String id, String accountNumber, BigDecimal balance, String accountHolder, List<Transaction> transactions) {
        super(EventsEnum.ACCOUNT_BALANCE_UPDATED.name());
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountHolder = accountHolder;
        this.transactions = transactions;
    }

    public AccountBalanceUpdated(String accountNumber, BigDecimal balance, String accountHolder) {
        super(EventsEnum.ACCOUNT_BALANCE_UPDATED.name());
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountHolder = accountHolder;
    }

    public AccountBalanceUpdated() {
        super(EventsEnum.ACCOUNT_BALANCE_UPDATED.name());
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}

