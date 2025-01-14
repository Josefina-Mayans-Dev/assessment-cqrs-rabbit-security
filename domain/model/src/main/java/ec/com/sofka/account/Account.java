package ec.com.sofka.account;

import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.account.values.objects.*;
import ec.com.sofka.account.values.objects.AccountNumber;
import ec.com.sofka.generics.utils.Entity;


public class Account extends Entity<AccountId> {
    private final Balance balance;
    private final AccountNumber accountNumber;
    private final AccountHolder accountHolder;
    private Transactions transactions;

    public Account(AccountId id, Balance balance, AccountNumber accountNumber, AccountHolder accountHolder, Transactions transactions) {
        super(id);
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.transactions = transactions;
    }

    public Account(AccountId id, Balance balance, AccountNumber accountNumber, AccountHolder accountHolder) {
        super(id);
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    public Balance getBalance() {
        return balance;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }
    public AccountHolder getAccountHolder() {
        return accountHolder;
    }
    public Transactions getTransactions(){ return transactions;}


}
