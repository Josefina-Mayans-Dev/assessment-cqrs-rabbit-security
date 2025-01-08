package ec.com.sofka.appservice.account.request;

import ec.com.sofka.generics.utils.Request;

import java.math.BigDecimal;

public class UpdateAccountRequest extends Request {
    private final BigDecimal balance;
    private final String accountNumber;
    private final String accountHolder;

    public UpdateAccountRequest(final String aggregateId, final BigDecimal balance, final String accountNumber, final String accountHolder) {
        super(aggregateId);
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

}