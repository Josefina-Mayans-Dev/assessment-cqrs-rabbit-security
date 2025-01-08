package ec.com.sofka.aggregate.customer;

import ec.com.sofka.account.Account;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.account.values.objects.*;
import ec.com.sofka.account.values.objects.AccountHolder;
import ec.com.sofka.account.values.objects.AccountNumber;
import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.generics.domain.DomainActionsContainer;

import java.util.List;
import java.util.stream.IntStream;

public class CustomerHandler extends DomainActionsContainer {
    public CustomerHandler(Customer customer) {

        addDomainActions((AccountCreated event) -> {
            Account account = new Account(
                    AccountId.of(event.getAccountId()),
                    Balance.of(event.getAccountBalance()),
                    AccountNumber.of(event.getAccountNumber()),
                    AccountHolder.of(event.getAccountHolder()),
                    Transactions.of(event.getTransactions()));
            customer.getAccounts().add(account);
            customer.setAccount(account);
        });

        addDomainActions((AccountBalanceUpdated event) -> {
            List<Account> accounts = customer.getAccounts();

            int index = IntStream.range(0, accounts.size())
                    .filter(i -> accounts.get(i).getAccountNumber().getValue().equals(event.getAccountNumber()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Account not found for update: " + event.getId()));

            Account updatedAccount = new Account(
                    AccountId.of(event.getId()),
                    Balance.of(event.getBalance()),
                    AccountNumber.of(event.getAccountNumber()),
                    AccountHolder.of(event.getAccountHolder()),
                    Transactions.of(event.getTransactions())
            );

            accounts.set(index, updatedAccount);
        });


    }
}
