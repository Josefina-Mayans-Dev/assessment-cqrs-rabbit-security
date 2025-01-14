package ec.com.sofka;

import ec.com.sofka.appservice.account.queries.query.GetAccountQuery;
import ec.com.sofka.data.AccountRequestDTO;
import ec.com.sofka.data.AccountResponseDTO;
import ec.com.sofka.handlers.AccountHandler;
import ec.com.sofka.routes.AccountRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(AccountRouter.class)  // Para cargar solo el enrutador
public class AccountRouterTest {

    @Autowired
    private WebTestClient webTestClient; // El cliente para hacer peticiones HTTP

    @MockitoBean
    private AccountHandler accountHandler; // Simulación del AccountHandler

    private AccountRequestDTO validAccountRequest;
    private AccountResponseDTO validAccountResponse;

    @BeforeEach
    void setUp() {
        // Inicializamos un DTO de solicitud y respuesta válidos
        validAccountRequest = new AccountRequestDTO( "1A", "123456", "John Doe", BigDecimal.valueOf(500.0));
        validAccountResponse = new AccountResponseDTO("1A", BigDecimal.valueOf(500.0), "123456","John Doe" );
    }

    @Test
    void testCreateAccountSuccess() {
        // Simulamos que el AccountHandler retorna una cuenta válida al crear una cuenta
        Mockito.when(accountHandler.createAccount(Mockito.any(AccountRequestDTO.class)))
                .thenReturn(Mono.just(validAccountResponse));

        webTestClient.post()
                .uri("/accounts/create")
                .bodyValue(validAccountRequest)
                .exchange()
                .expectStatus().isCreated()  // Verifica que el estado es 201
                .expectHeader().contentType("application/json")  // Verifica el tipo de contenido
                .expectBody(AccountResponseDTO.class)  // Verifica el tipo de respuesta
                .isEqualTo(validAccountResponse);  // Verifica que la respuesta es la esperada
    }

    @Test
    void testGetAllAccountsSuccess() {
        // Simulamos que el AccountHandler retorna una lista de cuentas
        Mockito.when(accountHandler.getAllAccounts(Mockito.any(GetAccountQuery.class)))
                .thenReturn(Flux.just(validAccountResponse));

        webTestClient.get()
                .uri("/accounts")
                .exchange()
                .expectStatus().isOk()  // Verifica que el estado es 200
                .expectHeader().contentType("application/json")  // Verifica el tipo de contenido
                .expectBodyList(AccountResponseDTO.class)  // Verifica el tipo de respuesta
                .hasSize(1)  // Verifica que hay 1 cuenta en la respuesta
                .contains(validAccountResponse);  // Verifica que la cuenta esperada está en la respuesta
    }

    @Test
    void testGetAllAccountsNoContent() {
        // Simulamos que no hay cuentas disponibles
        Mockito.when(accountHandler.getAllAccounts(Mockito.any(GetAccountQuery.class)))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/accounts")
                .exchange()
                .expectStatus().isNotFound()  // Verifica que el estado es 404 si no hay contenido
                .expectBody().isEmpty();  // Verifica que la respuesta está vacía
    }

    @Test
    void testUpdateAccountSuccess() {
        // Simulamos que el AccountHandler actualiza una cuenta correctamente
        Mockito.when(accountHandler.updateAccount(Mockito.any(AccountRequestDTO.class)))
                .thenReturn(Mono.just(validAccountResponse));

        webTestClient.put()
                .uri("/accounts")
                .bodyValue(validAccountRequest)
                .exchange()
                .expectStatus().isOk()  // Verifica que el estado es 200
                .expectHeader().contentType("application/json")  // Verifica el tipo de contenido
                .expectBody(AccountResponseDTO.class)  // Verifica el tipo de respuesta
                .isEqualTo(validAccountResponse);  // Verifica que la respuesta es la esperada
    }

    @Test
    void testUpdateAccountInternalServerError() {
        // Simulamos un error en el AccountHandler
        Mockito.when(accountHandler.updateAccount(Mockito.any(AccountRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Internal server error")));

        webTestClient.put()
                .uri("/accounts")
                .bodyValue(validAccountRequest)
                .exchange()
                .expectStatus().is5xxServerError()  // Verifica que el estado es 500
                .expectBody(String.class).isEqualTo("Error occurred while updating account");  // Verifica el mensaje de error
    }
}
