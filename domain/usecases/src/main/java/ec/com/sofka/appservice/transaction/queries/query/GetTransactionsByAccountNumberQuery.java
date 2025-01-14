package ec.com.sofka.appservice.transaction.queries.query;

import ec.com.sofka.generics.utils.Query;
import ec.com.sofka.generics.utils.Request;

public class GetTransactionsByAccountNumberQuery extends Query {
    private final String customerId;
    private final String accountNumber;

    public GetTransactionsByAccountNumberQuery(String customerId, String accountNumber) {
        super(null);
        this.customerId = customerId;
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}