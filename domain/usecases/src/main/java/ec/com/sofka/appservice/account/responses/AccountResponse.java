package ec.com.sofka.appservice.account.responses;

import ec.com.sofka.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AccountResponse {
    private final String customerId;
    private final String accountId;
    private final BigDecimal balance;
    private final String accountNumber;
    private final String accountHolder;
    private List<Transaction> transactions;

    public AccountResponse(String customerId, String accountId, BigDecimal balance, String accountNumber, String accountHolder, List<Transaction> transactions) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.transactions = transactions;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
