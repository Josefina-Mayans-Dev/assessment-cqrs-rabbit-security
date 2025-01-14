package ec.com.sofka.appservice.transaction.commands.usecases;

import ec.com.sofka.aggregate.action.Action;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.transaction.commands.RegisterTransactionCommand;
import ec.com.sofka.appservice.transaction.queries.responses.TransactionResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.TransactionRepository;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import ec.com.sofka.utils.enums.TransactionTypes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegisterTransactionUseCase implements IUseCaseExecute<RegisterTransactionCommand, TransactionResponse> {
    private final IEventStore repository;
   // private final TransactionRepository transactionRepository;
  //  private final AccountRepository accountRepository;
    private final BusEvent busEvent;

    public RegisterTransactionUseCase(IEventStore repository, BusEvent busEvent) {
        this.repository = repository;
        this.busEvent = busEvent;
    }

    @Override
    public Mono<TransactionResponse> execute(RegisterTransactionCommand cmd) {
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

                            action.getUncommittedEvents()
                                    .stream()
                                    .map(repository::save)
                                    .forEach(busEvent::sendEventTransactionRegistered);

                           action.markEventsAsCommitted();


                            BigDecimal balance = accountDTO.getBalance().subtract(cmd.getAmount()).subtract(fee);
                            accountDTO.setBalance(balance);

                                                    customer.updateAccountBalance(
                                                            accountDTO.getId(),
                                                            balance,
                                                            accountDTO.getAccountNumber(),
                                                            accountDTO.getAccountHolder()
                                                    );

                            customer.getUncommittedEvents()
                                    .stream()
                                    .map(repository::save)
                                    .forEach(busEvent::sendEventAccountUpdated);

                            customer.markEventsAsCommitted();

                            return Mono.just(new TransactionResponse(
                                  action.getId().getValue(),
                                  action.getTransaction().getAmount().getValue(),
                                  action.getTransaction().getFee().getValue(),
                                  action.getTransaction().getTransactionType().getValue(),
                                  action.getTransaction().getDate().getValue(),
                                  action.getTransaction().getAccountId().getValue(),
                                  customer.getId().getValue(),
                                  action.getTransaction().getDescription().getValue()
                                                            ));

                                    }));

    }
}
