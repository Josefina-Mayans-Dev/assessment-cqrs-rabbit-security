package ec.com.sofka;

import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.events.AccountUpdated;
import ec.com.sofka.aggregate.events.TransactionRegistered;
import ec.com.sofka.appservice.account.queries.usecases.AccountSavedViewUseCase;
import ec.com.sofka.appservice.transaction.queries.usecases.TransactionSavedViewUseCase;
import ec.com.sofka.gateway.BusEventListener;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class BusListener implements BusEventListener {

    private final RabbitEnvProps rabbitEnvProps;
    private final AccountSavedViewUseCase accountSavedViewUseCase;
    private final TransactionSavedViewUseCase transactionSavedViewUseCase;

    public BusListener(RabbitEnvProps rabbitEnvProps, AccountSavedViewUseCase accountSavedViewUseCase, TransactionSavedViewUseCase transactionSavedViewUseCase) {
        this.rabbitEnvProps = rabbitEnvProps;
        this.accountSavedViewUseCase = accountSavedViewUseCase;
        this.transactionSavedViewUseCase = transactionSavedViewUseCase;
    }

    @Override
    @RabbitListener(queues = "#{@rabbitEnvProps.getAccountQueue()}")
    public void receiveAccountCreated(DomainEvent event) {
        AccountCreated accountCreated = (AccountCreated) event;
        AccountDTO accountDTO = new AccountDTO(
                accountCreated.getAccountId(),
                accountCreated.getAccountNumber(),
                accountCreated.getAccountBalance(),
                accountCreated.getAccountHolder()
        );
        accountSavedViewUseCase.accept(accountDTO);
    }

    @Override
    @RabbitListener(queues = "#{@rabbitEnvProps.getTransactionQueue()}")
    public void receiveTransactionRegistered(DomainEvent event) {
        TransactionRegistered transactionCreated = (TransactionRegistered) event;
        TransactionDTO transactionDTO = new TransactionDTO(
                transactionCreated.getId(),
                transactionCreated.getAmount(),
                transactionCreated.getFee(),
                transactionCreated.getTransactionTypes(),
                transactionCreated.getDate(),
                transactionCreated.getDescription(),
                transactionCreated.getAccountId()
        );
        transactionSavedViewUseCase.accept(transactionDTO);
    }

    @Override
    @RabbitListener(queues = "#{@rabbitEnvProps.getAccountUpdatedQueue()}")
    public void receiveAccountBalanceUpdated(DomainEvent event) {
        if (event instanceof  AccountBalanceUpdated) {
        AccountBalanceUpdated accountBalanceUpdated = (AccountBalanceUpdated) event;
        AccountDTO accountDTO = new AccountDTO(
                accountBalanceUpdated.getId(),
                accountBalanceUpdated.getAccountNumber(),
                accountBalanceUpdated.getBalance(),
                accountBalanceUpdated.getAccountHolder()
        );
        accountSavedViewUseCase.accept(accountDTO); }
        else {
            // Log o maneja el error de manera adecuada si el tipo es incorrecto
            System.err.println("Evento no esperado: " + event.getClass().getSimpleName());
        }
    }

    @Override
    @RabbitListener(queues = "#{@rabbitEnvProps.getAccountUpdatedQueue()}")
    public void receiveAccountUpdated(DomainEvent event) {
        if (event instanceof  AccountUpdated || event instanceof  AccountBalanceUpdated)
        {        AccountUpdated accountUpdated = (AccountUpdated) event;
        AccountDTO accountDTO = new AccountDTO(
                accountUpdated.getAccountId(),
                accountUpdated.getAccountNumber(),
                accountUpdated.getBalance(),
                accountUpdated.getAccountHolder()
        );
        accountSavedViewUseCase.accept(accountDTO);
        } else {
            // Log o maneja el error de manera adecuada si el tipo es incorrecto
            System.err.println("Evento no esperado: " + event.getClass().getSimpleName());
        }
    }
}