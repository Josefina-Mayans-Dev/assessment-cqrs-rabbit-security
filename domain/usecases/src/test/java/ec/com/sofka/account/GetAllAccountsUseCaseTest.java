package ec.com.sofka.account;

import ec.com.sofka.appservice.account.queries.query.GetAccountQuery;
import ec.com.sofka.appservice.account.queries.responses.AccountResponse;
import ec.com.sofka.appservice.account.queries.usecases.GetAllAccountsUseCase;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.generics.utils.QueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GetAllAccountsUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    private GetAllAccountsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetAllAccountsUseCase(accountRepository);
    }

    @Test
    void shouldReturnAllAccountsSuccessfullyWhenAccountsExist() {
        GetAccountQuery query = new GetAccountQuery();

        // Datos de prueba para las cuentas
        AccountDTO accountDTO1 = new AccountDTO("account123", "1234567800",  BigDecimal.ZERO, "John Doe");
        AccountDTO accountDTO2 = new AccountDTO("account124", "8765432100", BigDecimal.TEN, "Jane Doe");

        // Simulamos que el repositorio encuentra las cuentas
        when(accountRepository.findAll())
                .thenReturn(Flux.just(accountDTO1, accountDTO2));

        Mono<QueryResponse<AccountResponse>> result = useCase.get(query);

        // Ejecutamos el caso de uso
        StepVerifier.create(result)
                .consumeNextWith(response -> {
                    // Verificamos que la respuesta contiene las cuentas correctas
                    assertEquals(2, response.getMultipleResults().size());

                    // Verificamos los datos de la primera cuenta
                    AccountResponse accountResponse1 = response.getMultipleResults().get(0);
                    assertEquals("account123", accountResponse1.getAccountId());
                    assertEquals("1234567800", accountResponse1.getAccountNumber());
                    assertEquals("John Doe", accountResponse1.getAccountHolder());
                    assertEquals(BigDecimal.ZERO, accountResponse1.getBalance());

                    // Verificamos los datos de la segunda cuenta
                    AccountResponse accountResponse2 = response.getMultipleResults().get(1);
                    assertEquals("account124", accountResponse2.getAccountId());
                    assertEquals("8765432100", accountResponse2.getAccountNumber());
                    assertEquals("Jane Doe", accountResponse2.getAccountHolder());
                    assertEquals(BigDecimal.TEN, accountResponse2.getBalance());
                })
                .verifyComplete();

        // Verificamos que el repositorio haya sido llamado para obtener todas las cuentas
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoAccountsExist() {
        GetAccountQuery query = new GetAccountQuery();

        // Simulamos que no hay cuentas en el repositorio
        when(accountRepository.findAll())
                .thenReturn(Flux.empty());

        Mono<QueryResponse<AccountResponse>> result = useCase.get(query);


        // Ejecutamos el caso de uso
        StepVerifier.create(result)
                .consumeNextWith(response -> {
                    // Verificamos que la respuesta esté vacía
                    assertEquals(0, response.getMultipleResults().size());
                })
                .verifyComplete();

        // Verificamos que el repositorio haya sido llamado para obtener todas las cuentas
        verify(accountRepository, times(1)).findAll();
    }
}