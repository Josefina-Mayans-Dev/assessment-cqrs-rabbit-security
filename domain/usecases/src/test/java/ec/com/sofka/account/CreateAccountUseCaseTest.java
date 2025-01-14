package ec.com.sofka.account;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ec.com.sofka.ConflictException;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.account.values.objects.AccountHolder;
import ec.com.sofka.account.values.objects.AccountNumber;
import ec.com.sofka.account.values.objects.Balance;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.appservice.account.commands.CreateAccountCommand;
import ec.com.sofka.appservice.account.commands.usecases.CreateAccountUseCase;
import ec.com.sofka.appservice.user.commands.CreateUserCommand;
import ec.com.sofka.appservice.user.commands.usecases.CreateUserUseCase;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.UserRepository;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.UserDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;

/*@ExtendWith(MockitoExtension.class)
public class CreateAccountUseCaseTest {

    @Mock
    private IEventStore repository;

    @Mock
    private BusEvent busEvent;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        // Datos de prueba
        String accountNumber = "0123456789";
        String customerName = "John Doe";
        BigDecimal balance = BigDecimal.ZERO;

        // Creamos los objetos de valor para la cuenta
        AccountId accountId = AccountId.of("account123");
        Balance accountBalance = Balance.of(balance);
        AccountNumber accountNumberObj = AccountNumber.of(accountNumber);
        AccountHolder accountHolder = AccountHolder.of(customerName);

        // Creamos la cuenta con los valores esperados
        Account account = new Account(accountId, accountBalance, accountNumberObj, accountHolder );

        // Creamos un AccountDTO que será guardado por el repositorio
        AccountDTO accountDTO = new AccountDTO(
                account.getId().getValue(),
                account.getAccountNumber().getValue(),
                account.getBalance().getValue(),
                account.getAccountHolder().getValue()

        );

        // Simulamos que no existe ninguna cuenta con el mismo número
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Mono.empty());  // No se encuentra ninguna cuenta existente

        // Creamos el comando para la prueba
        CreateAccountCommand command = new CreateAccountCommand("customer123", balance, accountNumber, customerName);

        // Simulamos que el repositorio de eventos guarda los eventos correctamente
        when(repository.save(any(DomainEvent.class)))
                .thenReturn(Mono.just(new AccountCreated()));  // Simulamos el guardado exitoso del evento

        // Simulamos que el bus de eventos envía el evento correctamente
        doNothing().when(busEvent).sendEventAccountCreated(any(Mono.class));

        // Ejecutamos el caso de uso
        StepVerifier.create(createAccountUseCase.execute(command))
                .consumeNextWith(response -> {
                    // Verificamos que la respuesta contiene los datos correctos
                    assertEquals(accountNumber, response.getAccountNumber());
                    assertEquals(customerName, response.getAccountHolder());
                    assertEquals(balance, response.getBalance());
                })
                .verifyComplete();

        // Verificamos que el repositorio de eventos haya sido llamado para guardar los eventos
        verify(repository, times(1)).save(any(DomainEvent.class));

        // Verificamos que el bus de eventos haya sido llamado para enviar el evento
        verify(busEvent, times(1)).sendEventAccountCreated(any(Mono.class));

        // Verificamos que el repositorio de cuentas **no** haya sido llamado
        verify(accountRepository, never()).save(any(AccountDTO.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenAccountNumberAlreadyExists() {
        // Datos de prueba
        String accountNumber = "0123456789";
        String customerName = "John Doe";
        BigDecimal balance = BigDecimal.ZERO;

        // Creamos el comando para la prueba
        CreateAccountCommand command = new CreateAccountCommand("customer123", balance, accountNumber, customerName);

        // Simulamos que ya existe una cuenta con el mismo número
        AccountDTO existingAccountDTO = new AccountDTO("account123", accountNumber, balance, customerName);
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Mono.just(existingAccountDTO));  // Simulamos una cuenta existente

        // Ejecutamos el caso de uso y verificamos que se lanza una excepción de tipo ConflictException
        StepVerifier.create(createAccountUseCase.execute(command))
                .expectErrorMatches(throwable -> throwable instanceof ConflictException &&
                        throwable.getMessage().equals("The account number is already registered."))
                .verify();

        // Verificamos que el repositorio de eventos **no** haya sido llamado
        verify(repository, never()).save(any(DomainEvent.class));

        // Verificamos que el bus de eventos **no** haya sido llamado
        verify(busEvent, never()).sendEventAccountCreated(any(Mono.class));

        // Verificamos que el repositorio de cuentas haya sido consultado una vez
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }*/

   /* @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
        createAccountUseCase = new CreateAccountUseCase(repository, busEvent);
    }

    @Test
    public void testExecute_Positive() {
        // Configurar datos de entrada
        CreateAccountCommand command = new CreateAccountCommand("customerId", 1000, "123456", "John Doe");

        // Simular la respuesta del repositorio
        Flux<DomainEvent> events = Flux.empty();  // Simular que no hay eventos previos

        when(repository.findAggregate(eq("customerId"), eq("customer"))).thenReturn(events);

        // Crear Customer mock
        Customer customer = mock(Customer.class);
        when(Customer.from(eq("customerId"), eq(events))).thenReturn(customer);
        when(customer.createAccount(eq(1000.0), eq("123456"), eq("John Doe"))).thenReturn(customer);

        // Simular eventos no confirmados
        when(customer.getUncommittedEvents()).thenReturn(List.of(mock(DomainEvent.class)));
        doNothing().when(repository).save(any(DomainEvent.class));  // No hacer nada al guardar un evento
        doNothing().when(busEvent).sendEventAccountCreated(any(DomainEvent.class));  // No hacer nada al enviar el evento

        // Llamar al método `execute` de `CreateAccountUseCase`
        Mono<AccountResponse> result = createAccountUseCase.execute(command);

        // Verificar el comportamiento
        StepVerifier.create(result)
                .expectNextMatches(accountResponse ->
                        accountResponse.getAccountNumber().equals("123456") &&
                                accountResponse.getBalance().equals(1000.0)
                )
                .expectComplete()
                .verify();

        // Verificar que el repositorio y el busEvent fueron llamados
        verify(repository, times(1)).findAggregate(eq("customerId"), eq("customer"));
        verify(repository, times(1)).save(any(DomainEvent.class));  // Verifica que el evento fue guardado
        verify(busEvent, times(1)).sendEventAccountCreated(any(DomainEvent.class));  // Verifica que el evento fue enviado
    }*//*




    *//*@Test
    public void testExecute_Negative_EventSaveError() {
        // Configurar datos de entrada
        CreateAccountCommand command = new CreateAccountCommand("customerId", 1000, "123456", "John Doe");

        // Simular la respuesta del repositorio
        Flux<DomainEvent> events = Flux.empty();  // Simular que no hay eventos previos

        when(repository.findAggregate(eq("customerId"), eq("customer"))).thenReturn(events);

        // Crear Customer mock
        Customer customer = mock(Customer.class);
        when(Customer.from(eq("customerId"), eq(events))).thenReturn(customer);
        when(customer.createAccount(eq(1000.0), eq("123456"), eq("John Doe"))).thenReturn(customer);

        // Simular que la llamada a save falla
        when(customer.getUncommittedEvents()).thenReturn(List.of(mock(DomainEvent.class)));
        when(repository.save(any(DomainEvent.class))).thenThrow(new RuntimeException("Database error"));

        // Llamar al método `execute` de `CreateAccountUseCase` y verificar que lanza un error
        Mono<AccountResponse> result = createAccountUseCase.execute(command);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        // Verificar que los métodos fueron llamados
        verify(repository, times(1)).findAggregate(eq("customerId"), eq("customer"));
        verify(repository, times(1)).save(any(DomainEvent.class));  // Verifica que la llamada a save ocurrió
        verify(busEvent, never()).sendEventAccountCreated(any(DomainEvent.class));  // No debería llamar a enviar el evento
    }

    @Test
    public void testExecute_Negative_CustomerCreationError() {
        // Configurar datos de entrada
        CreateAccountCommand command = new CreateAccountCommand("customerId", 1000, "123456", "John Doe");

        // Simular que el repositorio no encuentra eventos
        Flux<DomainEvent> events = Flux.empty();

        when(repository.findAggregate(eq("customerId"), eq("customer"))).thenReturn(events);

        // Crear Customer mock
        Customer customer = mock(Customer.class);
        when(Customer.from(eq("customerId"), eq(events))).thenReturn(customer);
        when(customer.createAccount(eq(1000.0), eq("123456"), eq("John Doe"))).thenThrow(new IllegalArgumentException("Invalid account data"));

        // Llamar al método `execute` y verificar que lanza un error
        Mono<AccountResponse> result = createAccountUseCase.execute(command);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        // Verificar que el repositorio y busEvent no fueron llamados
        verify(repository, times(1)).findAggregate(eq("customerId"), eq("customer"));
        verify(busEvent, never()).sendEventAccountCreated(any(DomainEvent.class));  // No debe enviarse evento
    }*//*
}*/

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private IEventStore eventStore;

    @Mock
    private BusEvent busEvent;

    @InjectMocks
    private CreateAccountUseCase useCase;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void execute() {
    }

    @Test
    void createAccountSuccessfully() {

        CreateAccountCommand command = new CreateAccountCommand( "customerid",
                BigDecimal.valueOf(1000.0),"123456789", "John Doe"
        );

        when(eventStore.findAllAggregatesBy("customer")).thenReturn(Flux.empty());


        StepVerifier.create(useCase.execute(command))
                .expectNextMatches(response -> {
                    return response.getAccountNumber().equals("123456789")
                            && response.getAccountHolder().equals("John Doe")
                            && Objects.equals(response.getBalance(), BigDecimal.valueOf(1000.0));
                })
                .verifyComplete();


        verify(eventStore, times(1)).findAllAggregatesBy("customer");
        verify(busEvent, times(1)).sendEventAccountCreated(any());
    }

    @Test
    void createAccountFailsWhenAccountExists() {

        CreateAccountCommand command = new CreateAccountCommand( "customerid",
                BigDecimal.valueOf(1000.0),"123456789", "John Doe"
        );


        DomainEvent existingEvent = mockDomainEventWithAccountNumber();
        when(eventStore.findAllAggregatesBy("customer"))
                .thenReturn(Flux.just(existingEvent));


        StepVerifier.create(useCase.execute(command))
                .expectErrorMessage("Account with number 123456789 already exists")
                .verify();



        verify(eventStore, never()).save(any());
        verify(busEvent, never()).sendEventAccountCreated(any());
    }


    private DomainEvent mockDomainEventWithAccountNumber() {
        AccountCreated event = new AccountCreated(
                "123456789",
                BigDecimal.valueOf(1000.00),
                "John Doe");
        event.setAggregateRootId("customerId");
        event.setAggregateRootName("customer");
        return event;
    }

}


