package ec.com.sofka.appservice.account.queries.query;

import ec.com.sofka.generics.utils.Query;

import java.math.BigDecimal;

public class GetAccountQuery extends Query {
    private BigDecimal balance;
    private String accountNumber;
    private String accountHolder;


    public GetAccountQuery(final String aggregateId, final String accountNumber) {
        super(aggregateId);
        this.balance = null;
        this.accountNumber = accountNumber;
        this.accountHolder = null;
    }

    public GetAccountQuery(){
        super(null);

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
