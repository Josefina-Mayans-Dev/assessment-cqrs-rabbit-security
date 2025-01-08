package ec.com.sofka.aggregate.events;

import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.transaction.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountCreated extends DomainEvent {
    private String accountId;
    private String accountNumber;
    private BigDecimal accountBalance;
    private String accountHolder;
    private List<Transaction> transactions = new ArrayList<>();

    public AccountCreated(String accountId, String accountNumber, BigDecimal accountBalance, String accountHolder, List<Transaction> transactions) {
        super(EventsEnum.ACCOUNT_CREATED.name());
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.accountHolder = accountHolder;
        this.transactions = transactions;
    }

    public AccountCreated(String accountNumber, BigDecimal accountBalance, String accountHolder) {
        super(EventsEnum.ACCOUNT_CREATED.name());
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.accountHolder = accountHolder;
    }

    public AccountCreated() {
        super(EventsEnum.ACCOUNT_CREATED.name());

    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public List<Transaction> getTransactions() {return transactions;}


}
