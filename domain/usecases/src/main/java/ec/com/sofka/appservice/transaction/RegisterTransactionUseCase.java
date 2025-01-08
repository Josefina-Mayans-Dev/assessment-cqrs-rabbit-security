package ec.com.sofka.appservice.transaction;

import ec.com.sofka.aggregate.action.Action;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.transaction.request.RegisterTransactionRequest;
import ec.com.sofka.appservice.transaction.responses.TransactionResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.BusMessage;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.TransactionRepository;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import ec.com.sofka.log.Log;
import ec.com.sofka.utils.enums.TransactionTypes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegisterTransactionUseCase implements IUseCaseExecute<RegisterTransactionRequest, TransactionResponse> {
    private final IEventStore repository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BusMessage busMessage;

    public RegisterTransactionUseCase(IEventStore repository, AccountRepository accountRepository, TransactionRepository transactionRepository, BusMessage busMessage) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.busMessage = busMessage;
    }

    @Override
    public Mono<TransactionResponse> execute(RegisterTransactionRequest cmd) {
        Flux<DomainEvent> eventsCustomer = repository.findAggregate(cmd.getCustomerId(), "customer");

        return Customer.from(cmd.getCustomerId(), eventsCustomer)
                .flatMap(customer -> Mono.justOrEmpty(
                                customer.getAccounts().stream()
                                        .filter(account -> account.getAccountNumber().getValue().equals(cmd.getAccountNumber()))
                                        .findFirst()
                        )
                        .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                        .flatMap(account -> {
                            AccountDTO accountDTO = new AccountDTO(
                                    account.getId().getValue(),
                                    account.getAccountNumber().getValue(),
                                    account.getBalance().getValue(),
                                    account.getAccountHolder().getValue()
                            );


                            TransactionTypes transactionTypes = cmd.getTransactionTypes();
                            BigDecimal fee = transactionTypes.getFee();
                            BigDecimal totalAmount = cmd.getAmount().add(fee);

                            if (accountDTO.getBalance().compareTo(totalAmount) < 0) {
                                return Mono.error(new RuntimeException("Insufficient balance for this transaction."));
                            }

                            Action action = new Action();

                            action.registerTransaction(
                                    transactionTypes,
                                    cmd.getAmount(),
                                    fee,
                                    LocalDateTime.now(),
                                    cmd.getDescription(),
                                    accountDTO.getId()
                            );

                        /*    Log log = new Log("New transaction was registered for account: " + account.getAccountNumber().getValue(), "transaction", null);
                            busMessage.sendMsg(log);*/


                            return transactionRepository.save(new TransactionDTO(
                                            action.getTransaction().getId().getValue(),
                                            action.getTransaction().getAmount().getValue(),
                                            action.getTransaction().getFee().getValue(),
                                            action.getTransaction().getTransactionType().getValue(),
                                            action.getTransaction().getDate().getValue(),
                                            action.getTransaction().getDescription().getValue(),
                                            action.getTransaction().getAccountId().getValue()
                                    ))
                                    .flatMap(transactionDTO -> {
                                        BigDecimal balance = accountDTO.getBalance().subtract(cmd.getAmount()).subtract(fee);
                                        accountDTO.setBalance(balance);
                                        return accountRepository.save(accountDTO)
                                                .flatMap(savedAccount -> {
                                                    customer.updateAccountBalance(
                                                            savedAccount.getId(),
                                                            balance,
                                                            savedAccount.getAccountNumber(),
                                                            savedAccount.getAccountHolder()
                                                    );

                                                    return Flux.concat(
                                                                    Flux.fromIterable(action.getUncommittedEvents())
                                                                            .flatMap(repository::save),
                                                                    Flux.fromIterable(customer.getUncommittedEvents())
                                                                            .flatMap(repository::save)
                                                            )
                                                            .doOnTerminate(() -> {
                                                                action.markEventsAsCommitted();
                                                                customer.markEventsAsCommitted();
                                                            })
                                                            .then()
                                                            .thenReturn(new TransactionResponse(
                                                                    action.getId().getValue(),
                                                                    action.getTransaction().getAmount().getValue(),
                                                                    action.getTransaction().getFee().getValue(),
                                                                    action.getTransaction().getTransactionType().getValue(),
                                                                    action.getTransaction().getDate().getValue(),
                                                                    action.getTransaction().getAccountId().getValue(),
                                                                    customer.getId().getValue(),
                                                                    action.getTransaction().getDescription().getValue()
                                                            ));
                                                });
                                    });
                        }));
    }
}
