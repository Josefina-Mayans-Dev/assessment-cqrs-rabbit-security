package ec.com.sofka.appservice.account.commands;

import ec.com.sofka.generics.utils.Command;

import java.math.BigDecimal;

//Usage of the Request class
public class CreateAccountCommand extends Command
{
    private final BigDecimal balance;
    private final String accountNumber;
    private final String accountHolder;


    public CreateAccountCommand(final String aggregateId, final BigDecimal balance, final String accountNumber, final String accountHolder) {
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
