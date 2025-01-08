package ec.com.sofka.appservice.account.request;

import ec.com.sofka.generics.utils.Request;

import java.math.BigDecimal;

//Usage of the Request class
public class CreateAccountRequest extends Request
{
    private final BigDecimal balance;
    private final String accountNumber;
    private final String accountHolder;


    public CreateAccountRequest(final String aggregateId, final BigDecimal balance, final String accountNumber, final String accountHolder) {
        super(aggregateId);
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public String getNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return accountHolder;
    }

}
