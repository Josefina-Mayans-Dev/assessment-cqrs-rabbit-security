package ec.com.sofka.appservice.transaction.request;

import ec.com.sofka.generics.utils.Request;

public class GetTransactionsByAccountNumberRequest extends Request {
    private final String customerId;
    private final String accountNumber;

    public GetTransactionsByAccountNumberRequest(String customerId, String accountNumber) {
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