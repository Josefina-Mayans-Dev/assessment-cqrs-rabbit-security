package ec.com.sofka.transaction;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ec.com.sofka.account.Account;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.account.values.objects.AccountHolder;
import ec.com.sofka.account.values.objects.AccountNumber;
import ec.com.sofka.account.values.objects.Balance;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.appservice.transaction.commands.RegisterTransactionCommand;
import ec.com.sofka.appservice.transaction.commands.usecases.RegisterTransactionUseCase;
import ec.com.sofka.appservice.transaction.queries.responses.TransactionResponse;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.utils.enums.TransactionTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RegisterTransactionUseCaseTest {

    @Mock
    private IEventStore repository;

    @Mock
    private BusEvent busEvent;

    @InjectMocks
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Test
    void shouldRegisterTransactionSuccessfully() {
        String customerId = "customer-123";
        String accountId = "account-123";
        String accountNumber = "123456789";
        BigDecimal initialBalance = BigDecimal.valueOf(500.0);
        BigDecimal transactionAmount = BigDecimal.valueOf(100.0);
        BigDecimal fee = BigDecimal.valueOf(5.0);
        BigDecimal netAmount = transactionAmount.add(fee);  // Total amount to be deducted from balance
        BigDecimal expectedBalance = initialBalance.subtract(transactionAmount).subtract(fee);  // New balance after transaction

        // Mockear evento de cuenta creada
        AccountCreated accountCreatedEvent = new AccountCreated(accountId, accountNumber, initialBalance, "user-123", new ArrayList<>());
        accountCreatedEvent.setAggregateRootId(customerId);

        // Simulamos que se encuentra el evento de cuenta
        when(repository.findAggregate(customerId, "customer")).thenReturn(Flux.just(accountCreatedEvent));
        when(repository.save(any(DomainEvent.class))).thenReturn(Mono.empty());  // Simulamos la persistencia del evento

        // Crear el comando para realizar la transacción
        RegisterTransactionCommand cmd = new RegisterTransactionCommand(transactionAmount, TransactionTypes.DEPOSIT_ATM, accountNumber, customerId, "Transaction description");

        // Ejecutar el caso de uso
        Mono<TransactionResponse> result = registerTransactionUseCase.execute(cmd);

        StepVerifier.create(result)
                .assertNext(response -> {
                    // Verificar la respuesta
                    assertNotNull(response);
                    assertEquals(netAmount, response.getAmount());
                    assertEquals(fee, response.getFee());
                    assertEquals(TransactionTypes.DEPOSIT_ATM, response.getTransactionTypes());
                  //  assertEquals(expectedBalance, response.getFee());  // Verificar que el balance ha sido actualizado
                })
                .verifyComplete();

        // Verificar las interacciones con el repositorio
        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(repository, times(1)).save(any(DomainEvent.class));  // Verificar que los eventos fueron guardados
    }

    @Test
    void shouldFailWhenInsufficientBalanceForTransaction() {
        String customerId = "customer-123";
        String accountNumber = "123456789";
        BigDecimal initialBalance = BigDecimal.valueOf(50.0);  // Balance insuficiente
        BigDecimal transactionAmount = BigDecimal.valueOf(100.0);  // Monto a retirar
        BigDecimal fee = BigDecimal.valueOf(5.0);  // Fee

        // Crear evento de cuenta creada con saldo insuficiente
        AccountCreated accountCreatedEvent = new AccountCreated("account-123", accountNumber, initialBalance, "user-123", new ArrayList<>());
        accountCreatedEvent.setAggregateRootId(customerId);

        // Simular que se encuentra la cuenta
        when(repository.findAggregate(customerId, "customer")).thenReturn(Flux.just(accountCreatedEvent));

        // Crear el comando para realizar la transacción
        RegisterTransactionCommand cmd = new RegisterTransactionCommand(transactionAmount, TransactionTypes.WITHDRAW_ATM, accountNumber, customerId, "Withdrawal description");

        // Ejecutar el caso de uso y verificar que falla debido a fondos insuficientes
        Mono<TransactionResponse> result = registerTransactionUseCase.execute(cmd);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Insufficient balance for this transaction."))
                .verify();

        // Verificar las interacciones con el repositorio
        verify(repository, times(1)).findAggregate(customerId, "customer");
    }


/*    @Test
    void processAccountDepositTransaction_Success() {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("123456789");
       RegisterTransactionCommand command = new RegisterTransactionCommand(
                 BigDecimal.valueOf(100.0),
                null,
                null,
                "customerId",
                "description"
        );

        DomainEvent senderEvent = mockDomainEventWithAccountNumber("customerId");
        DomainEvent receiverEvent = mockDomainEventWithAccountNumber("receiverCustomerId");


        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.just(senderEvent));
        when(repository.findAggregate(command.getReceiverCustomerId())).thenReturn(Flux.just(receiverEvent));
        doNothing().when(busEvent).sendEventTransactionCreated(any());


        StepVerifier.create(useCase.execute(command))
                .expectNextMatches(response -> response.getTransactionType().equals("BA") &&
                        response.getAmount().compareTo(BigDecimal.valueOf(100.0)) == 0 &&
                        response.getTransactionFee().compareTo(BigDecimal.valueOf(2.0)) == 0)
                .verifyComplete();


        verify(repository, times(2)).findAggregate(anyString());
        verify(busEvent, atLeastOnce()).sendEventTransactionCreated(any());
    }

    @Test
    void processATMTransaction_Success() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("123456789");
        AtmTransactionCommand command = new AtmTransactionCommand(
                null,
                null,
                "customerId",
                "description",
                BigDecimal.valueOf(100.0),
                "ATM",
                BigDecimal.valueOf(2.0),
                null,
                new CreateCardCommand(
                        "customerId",
                        "MyCard",
                        "1234567890123456",
                        "VISA",
                        "ACTIVE",
                        "12/25",
                        "123",
                        BigDecimal.valueOf(10000.00),
                        "John Doe",
                        accountDTO
                ),
                "ATM NAME",
                "DEPOSIT"

        );
        DomainEvent senderEvent = mockDomainEventWithAccountNumber("customerId");
        DomainEvent cardEvent = mockDomainEventWithCardNumber();


        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.just(senderEvent, cardEvent));
        doNothing().when(busEvent).sendEventTransactionCreated(any());


        StepVerifier.create(useCase.execute(command))
                .expectNextMatches(response -> response.getTransactionType().equals("ATM") &&
                        response.getAmount().compareTo(BigDecimal.valueOf(100.0)) == 0 &&
                        response.getTransactionFee().compareTo(BigDecimal.valueOf(2.0)) == 0)
                .verifyComplete();


        verify(repository, times(1)).findAggregate(anyString());
        verify(busEvent, atLeastOnce()).sendEventTransactionCreated(any());
    }

    @Test
    void processAccountDepositTransaction_ReceiverAccountNotFound() {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("123456789");
        AccountDepositCommand command = new AccountDepositCommand(
                null,
                null,
                "customerId",
                "description",
                BigDecimal.valueOf(100.0),
                "BA",
                BigDecimal.valueOf(2.0),
                null,
                null,
                accountDTO,
                "receiverCustomerId"
        );

        DomainEvent senderEvent = mockDomainEventWithAccountNumber("customerId");
        DomainEvent receiverEvent = mockDomainEventWithAccountNumber("receiverCustomerId");


        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.just(senderEvent));
        when(repository.findAggregate(command.getReceiverCustomerId())).thenReturn(Flux.empty());



        StepVerifier.create(useCase.execute(command))
                .expectErrorMessage("Receiver account not found")
                .verify();


        verify(repository, times(2)).findAggregate(anyString());
        verify(busEvent, never()).sendEventTransactionCreated(any());
    }

    @Test
    void processAccountDepositTransaction_SenderAccountNotFound() {

        AccountDepositCommand command = new AccountDepositCommand(
                null,
                null,
                "customerId",
                "description",
                BigDecimal.valueOf(100.0),
                "BA",
                BigDecimal.valueOf(2.0),
                null,
                null,
                null,
                "receiverCustomerId"
        );

        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.empty());


        StepVerifier.create(useCase.execute(command))
                .expectErrorMessage("Sender account not found")
                .verify();


        verify(repository, times(2)).findAggregate(anyString());
        verify(busEvent, never()).sendEventTransactionCreated(any());
    }

    @Test
    void processTransaction_CardNotFound() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("123456789");
        AtmTransactionCommand command = new AtmTransactionCommand(
                null,
                null,
                "customerId",
                "description",
                BigDecimal.valueOf(100.0),
                "ATM",
                BigDecimal.valueOf(2.0),
                null,
                new CreateCardCommand(
                        "customerId",
                        "MyCard",
                        "1234567890123456",
                        "VISA",
                        "ACTIVE",
                        "12/25",
                        "123",
                        BigDecimal.valueOf(10000.00),
                        "John Doe",
                        accountDTO
                ),
                "ATM NAME",
                "DEPOSIT"

        );

        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.empty());


        StepVerifier.create(useCase.execute(command))
                .expectErrorMessage("Getting card failed")
                .verify();


        verify(repository, times(1)).findAggregate(anyString());
        verify(busEvent, never()).sendEventTransactionCreated(any());
    }

    @Test
    void processTransaction_Account_CardNotFound() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("123456789");
        AtmTransactionCommand command = new AtmTransactionCommand(
                null,
                null,
                "customerId",
                "description",
                BigDecimal.valueOf(100.0),
                "ATM",
                BigDecimal.valueOf(2.0),
                null,
                new CreateCardCommand(
                        "customerId",
                        "MyCard",
                        "1234567890123456",
                        "VISA",
                        "ACTIVE",
                        "12/25",
                        "123",
                        BigDecimal.valueOf(10000.00),
                        "John Doe",
                        accountDTO
                ),
                "ATM NAME",
                "DEPOSIT"

        );




        DomainEvent senderEvent = mockDomainEventWithAccountNumber("customerId");
        DomainEvent cardEvent = mockDomainEventWithCardNumber();


        when(repository.findAggregate(command.getCustomerId())).thenReturn(Flux.just(cardEvent));

        StepVerifier.create(useCase.execute(command))
                .expectErrorMessage("Account or Card not found in store")
                .verify();


        verify(repository, times(1)).findAggregate(anyString());
        verify(busEvent, never()).sendEventTransactionCreated(any());
    }


    private DomainEvent mockDomainEventWithAccountNumber(String customerId) {
        AccountCreated event = new AccountCreated(
                null,
                "123456789",
                BigDecimal.valueOf(1000.00),
                "John Doe",
                "Savings");

        event.setAggregateRootId(customerId);
        event.setAggregateRootName("customer");
        return event;
    }

    private DomainEvent mockDomainEventWithCardNumber() {
        CardCreated event = new CardCreated(
                "cardId", "Test Card", "1234567890123456",
                "Credit", "Active", "12/30", "1234",
                BigDecimal.valueOf(5000.00), "John Doe", new Account(
                AccountId.of("accountId"),
                Balance.of(BigDecimal.valueOf(1000.00)),
                AccountNumber.of("123456789"),
                OwnerName.of("John Doe"),
                AccountType.of("Savings")
        )
        );

        event.setAggregateRootId("customerId");
        return event;
    }

    // Métodos auxiliares para eventos simulados
    private DomainEvent mockSenderEvent() {
        return new TransactionCreated(
                new TransactionId().getValue(),
                "description",
                BigDecimal.valueOf(100.0),
                "BA",
                BigDecimal.valueOf(2.0),
                LocalDateTime.now(),
                new Account(
                        AccountId.of("accountId"),
                        Balance.of(BigDecimal.valueOf(1000.00)),
                        AccountNumber.of("123456789"),
                        OwnerName.of("John Doe"),
                        AccountType.of("Savings")
                ),
                null,
                null,
                null,
                new Account(
                        AccountId.of("accountId"),
                        Balance.of(BigDecimal.valueOf(1000.00)),
                        AccountNumber.of("123456789"),
                        OwnerName.of("John Doe"),
                        AccountType.of("Savings")
                ),
                null,
                null,
                null
        );
    }*/

    /*@Test
    void shouldCreateATMDepositTransactionSuccessfully() {
        String customerId = "customer-123";
        String accountId = "account-123";
        String accountNumber = "123456789";
        BigDecimal balance = BigDecimal.valueOf(500.0);
        BigDecimal amount = BigDecimal.valueOf(100.0);
        BigDecimal fee = BigDecimal.valueOf(2.0);
        BigDecimal finalAmount = amount.add(fee);
        BigDecimal expectedBalance = balance.add(amount);

        AccountCreated accountCreatedEvent = new AccountCreated(
                accountId,
                accountNumber,
                balance,
                "user-123",
                new ArrayList<>()
        );
        accountCreatedEvent.setAggregateRootId(customerId);

        when(repository.findAggregate(customerId, "customer"))
                .thenReturn(Flux.just(accountCreatedEvent));
        when(repository.save(any(DomainEvent.class)))
                .thenReturn(Mono.empty());

        when(TransactionTypes.DEPOSIT_ATM).thenReturn();
        when(atmDepositStrategy.calculateFee()).thenReturn(fee);
        when(atmDepositStrategy.calculateBalance(initialBalance, depositAmount)).thenReturn(expectedBalance);

        TransactionDTO transactionDTO = new TransactionDTO(
                "transaction-123",
                depositAmount,
                fee,
                netAmount,
                TransactionTypes.DEPOSIT_ATM,
                LocalDateTime.now(),
                accountId
        );

        AccountDTO updatedAccountDTO = new AccountDTO(
                accountId,
                accountNumber,
                expectedBalance,
                "user-123"
        );

        RegisterTransactionCommand request = new RegisterTransactionCommand(
                depositAmount,
                TransactionTypes.DEPOSIT_ATM,
                accountNumber,
                customerId
        );

        Mono<TransactionResponse> responseMono = registerTransactionUseCase.execute(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(netAmount, response.getNetAmount());
                    assertEquals(fee, response.getFee());
                    assertEquals(TransactionTypes.DEPOSIT_ATM, response.getTransactionTypes());
                    assertEquals(expectedBalance, updatedAccountDTO.getBalance());
                })
                .verifyComplete();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(strategyFactory, times(1)).getStrategy(TransactionTypes.DEPOSIT_ATM);
        verify(atmDepositStrategy, times(1)).calculateFee();
        verify(atmDepositStrategy, times(1)).calculateBalance(initialBalance, depositAmount);
    }

    @Test
    void shouldCreateATMWithdrawTransactionSuccessfully() {
        String customerId = "customer-123";
        String accountId = "account-123";
        String accountNumber = "123456789";
        BigDecimal initialBalance = BigDecimal.valueOf(500.0);
        BigDecimal withdrawAmount = BigDecimal.valueOf(100.0);
        BigDecimal fee = BigDecimal.valueOf(2.0);
        BigDecimal netAmount = withdrawAmount.add(fee);
        BigDecimal expectedBalance = initialBalance.subtract(withdrawAmount).subtract(fee);

        AccountCreated accountCreatedEvent = new AccountCreated(
                accountId,
                accountNumber,
                initialBalance,
                "user-123"
        );
        accountCreatedEvent.setAggregateRootId(customerId);

        when(repository.findAggregate(customerId, "customer"))
                .thenReturn(Flux.just(accountCreatedEvent));
        when(repository.save(any(DomainEvent.class)))
                .thenReturn(Mono.empty());

        when(strategyFactory.getStrategy(TransactionTypes.WITHDRAW_ATM)).thenReturn(atmWithdrawalStrategy);
        when(atmWithdrawalStrategy.calculateFee()).thenReturn(fee);
        when(atmWithdrawalStrategy.calculateBalance(initialBalance, withdrawAmount)).thenReturn(expectedBalance);

        TransactionDTO transactionDTO = new TransactionDTO(
                "transaction-123",
                withdrawAmount,
                fee,
                netAmount,
                TransactionTypes.WITHDRAW_ATM,
                LocalDateTime.now(),
                accountId
        );

        AccountDTO updatedAccountDTO = new AccountDTO(
                accountId,
                accountNumber,
                expectedBalance,
                "user-123"
        );

        RegisterTransactionCommand request = new RegisterTransactionCommand(
                withdrawAmount,
                TransactionTypes.WITHDRAW_ATM,
                accountNumber,
                customerId
        );

        Mono<TransactionResponse> responseMono = registerTransactionUseCase.execute(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(netAmount, response.getNetAmount());
                    assertEquals(fee, response.getFee());
                    assertEquals(TransactionTypes.WITHDRAW_ATM, response.getTransactionTypes());
                    assertEquals(expectedBalance, updatedAccountDTO.getBalance());
                })
                .verifyComplete();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(strategyFactory, times(1)).getStrategy(TransactionTypes.WITHDRAW_ATM);
        verify(atmWithdrawalStrategy, times(1)).calculateFee();
        verify(atmWithdrawalStrategy, times(1)).calculateBalance(initialBalance, withdrawAmount);
    }

    @Test
    void shouldFailWhenInsufficientFundsForATMWithdraw() {
        String customerId = "customer-123";
        String accountId = "account-123";
        String accountNumber = "123456789";
        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
        BigDecimal withdrawAmount = BigDecimal.valueOf(200.0);
        BigDecimal fee = BigDecimal.valueOf(2.0);

        AccountCreated accountCreatedEvent = new AccountCreated(
                accountId,
                accountNumber,
                initialBalance,
                "user-123"
        );
        accountCreatedEvent.setAggregateRootId(customerId);

        when(repository.findAggregate(customerId, "customer"))
                .thenReturn(Flux.just(accountCreatedEvent));
        when(strategyFactory.getStrategy(TransactionTypes.WITHDRAW_ATM)).thenReturn(atmWithdrawalStrategy);
        when(atmWithdrawalStrategy.calculateFee()).thenReturn(fee);
        when(atmWithdrawalStrategy.calculateBalance(initialBalance, withdrawAmount))
                .thenThrow(new IllegalArgumentException("Insufficient funds"));

        RegisterTransactionCommand request = new RegisterTransactionCommand(
                withdrawAmount,
                TransactionTypes.WITHDRAW_ATM,
                accountNumber,
                customerId
        );

        Mono<TransactionResponse> responseMono = registerTransactionUseCase.execute(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Insufficient funds"))
                .verify();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(strategyFactory, times(1)).getStrategy(TransactionTypes.WITHDRAW_ATM);
        verify(atmWithdrawalStrategy, times(1)).calculateFee();
        verify(atmWithdrawalStrategy, times(1)).calculateBalance(initialBalance, withdrawAmount);
    }

    @Test
    void shouldFailWhenAccountNotFound() {
        String customerId = "customer-123";
        String accountNumber = "123456789";

        when(repository.findAggregate(customerId, "customer"))
                .thenReturn(Flux.empty());

        RegisterTransactionCommand request = new RegisterTransactionCommand(
                BigDecimal.valueOf(100.0),
                TransactionTypes.WITHDRAW_ATM,
                accountNumber,
                customerId
        );

        Mono<TransactionResponse> responseMono = registerTransactionUseCase.execute(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Account not found"))
                .verify();

        verify(repository, times(1)).findAggregate(customerId, "customer");
    }*/

    /*@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registerTransactionUseCase = new RegisterTransactionUseCase(repository, busEvent);
    }

    @Test
    public void testRegisterTransaction_Successful() {
        // Crear datos de entrada
        RegisterTransactionCommand cmd = new RegisterTransactionCommand(
                new BigDecimal("100.00"),  TransactionTypes.DEPOSIT_ATM, "accountNumber", "customerId", "Transaction description"
        );

        // Crear Customer y Account simulados
        Customer customer = mock(Customer.class);
        Account account = mock(Account.class);
        AccountDTO accountDTO = new AccountDTO("accountId", "accountNumber", new BigDecimal("500.00"), "Account Holder");

        // Simular la búsqueda de la cuenta
        when(repository.findAggregate(cmd.getCustomerId(), "customer")).thenReturn(Flux.just(new DomainEvent()));
        when(Customer.from(cmd.getCustomerId(), Flux.just(new DomainEvent()))).thenReturn(Mono.just(customer));
        when(customer.getAccounts()).thenReturn(List.of(account));
        when(account.getAccountNumber()).thenReturn(new AccountNumber("accountNumber"));
        when(account.getBalance()).thenReturn(new Balance(new BigDecimal("500.00")));
        when(account.getId()).thenReturn(new AccountId("accountId"));
        when(account.getAccountHolder()).thenReturn(new AccountHolder("Account Holder"));

        // Simular el proceso de registro de la transacción
        when(repository.save(any(DomainEvent.class))).thenReturn(Mono.just(new DomainEvent()));

        // Ejecutar el caso de uso
        Mono<TransactionResponse> result = registerTransactionUseCase.execute(cmd);

        StepVerifier.create(result)
                .expectNextMatches(transactionResponse -> {
                    // Verificar los valores de la respuesta
                    assertNotNull(transactionResponse);
                    assertEquals("accountId", transactionResponse.getAccountId());
                    assertEquals("customerId", transactionResponse.getCustomerId());
                    assertEquals("100.00", transactionResponse.getAmount());
                    assertEquals("2.00", transactionResponse.getFee());
                    assertEquals("DEPOSIT_ATM", transactionResponse.getTransactionTypes());
                    return true;
                })
                .expectComplete()
                .verify();

        // Verificar las interacciones con el repositorio
        verify(repository, times(1)).findAggregate(cmd.getCustomerId(), "customer");
        verify(repository, times(1)).save(any(DomainEvent.class));
    }

    @Test
    public void testRegisterTransaction_AccountNotFound() {
        // Crear datos de entrada
        RegisterTransactionCommand cmd = new RegisterTransactionCommand(
                new BigDecimal("100.00"), TransactionTypes.DEPOSIT_ATM, "nonExistentAccountNumber", "customerId", "Transaction description"
        );

        // Simular que no se encuentra la cuenta
        when(repository.findAggregate(cmd.getCustomerId(), "customer")).thenReturn(Flux.just(new DomainEvent()));
        when(Customer.from(cmd.getCustomerId(), Flux.just(new DomainEvent()))).thenReturn(Mono.just(new Customer()));

        // Ejecutar el caso de uso y verificar que se lanza el error adecuado
        Mono<TransactionResponse> result = registerTransactionUseCase.execute(cmd);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Account not found"))
                .verify();

        // Verificar las interacciones con el repositorio
        verify(repository, times(1)).findAggregate(cmd.getCustomerId(), "customer");
    }

    @Test
    public void testRegisterTransaction_InsufficientBalance() {
        // Crear datos de entrada
        RegisterTransactionCommand cmd = new RegisterTransactionCommand(
                new BigDecimal("600.00"), TransactionTypes.WITHDRAW_ATM,"accountNumber","customerId",  "Transaction description"
        );

        // Crear Customer y Account simulados con saldo insuficiente
        Customer customer = mock(Customer.class);
        Account account = mock(Account.class);
        AccountDTO accountDTO = new AccountDTO("accountId", "accountNumber", new BigDecimal("500.00"), "Account Holder");

        // Simular la búsqueda de la cuenta
        when(repository.findAggregate(cmd.getCustomerId(), "customer")).thenReturn(Flux.just(new DomainEvent()));
        when(Customer.from(cmd.getCustomerId(), Flux.just(new DomainEvent()))).thenReturn(Mono.just(customer));
        when(customer.getAccounts()).thenReturn(List.of(account));
        when(account.getAccountNumber()).thenReturn(new AccountNumber("accountNumber"));
        when(account.getBalance()).thenReturn(new Balance(new BigDecimal("500.00")));
        when(account.getId()).thenReturn(new AccountId("accountId"));
        when(account.getAccountHolder()).thenReturn(new AccountHolder("Account Holder"));

        // Ejecutar el caso de uso y verificar que se lanza el error adecuado
        Mono<TransactionResponse> result = registerTransactionUseCase.execute(cmd);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Insufficient balance for this transaction."))
                .verify();

        // Verificar las interacciones con el repositorio
        verify(repository, times(1)).findAggregate(cmd.getCustomerId(), "customer");
    }*/

}
