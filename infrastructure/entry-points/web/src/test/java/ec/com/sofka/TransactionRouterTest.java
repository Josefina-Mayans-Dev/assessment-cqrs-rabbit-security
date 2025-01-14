package ec.com.sofka;

import ec.com.sofka.data.AccountRequestDTO;
import ec.com.sofka.data.AccountResponseDTO;
import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;
import ec.com.sofka.handlers.AccountHandler;
import ec.com.sofka.handlers.TransactionHandler;
import ec.com.sofka.routes.AccountRouter;
import ec.com.sofka.utils.enums.TransactionTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@WebFluxTest(AccountRouter.class)  // Para cargar solo el enrutador
public class TransactionRouterTest {

    @Autowired
    private WebTestClient webTestClient; // El cliente para hacer peticiones HTTP

    @MockitoBean
    private TransactionHandler transactionHandler;

    private TransactionRequestDTO validTransactionRequest;
    private TransactionResponseDTO validTransactionResponse;

  /*  @BeforeEach
    void setUp() {
        // Inicializamos un DTO de solicitud y respuesta válidos
        validTransactionRequest = new TransactionRequestDTO( "1A", "123456", "John Doe", BigDecimal.valueOf(500.0));
        validTransactionResponse = new TransactionResponseDTO("1A", BigDecimal.valueOf(500.0), "123456","John Doe" );
    }*/

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Preparación de los datos para la transacción
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO(
                BigDecimal.valueOf(100.0),
                TransactionTypes.DEPOSIT_ATM,
                "account-123",  // Account number
                "Depositing money",
                "1A"
        );

        // Respuesta esperada para la transacción
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO(
                "transaction-123",  // Transaction ID
                BigDecimal.valueOf(100.0),  // Amount
                BigDecimal.valueOf(2.0),
                TransactionTypes.DEPOSIT_ATM,
                LocalDateTime.now(),
                "1A",
                "Depositing money"

        );

        // Simulamos la llamada al handler
        Mockito.when(transactionHandler.registerTransaction(Mockito.any(TransactionRequestDTO.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Realizamos la petición usando WebTestClient
        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)  // El cuerpo de la solicitud
                .exchange()
                .expectStatus().isCreated()  // Esperamos un código de estado 201
                .expectBody(TransactionResponseDTO.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(expectedResponse.getActionId(), response.getActionId());
                    assertEquals(expectedResponse.getCustomerId(), response.getCustomerId());
                    assertEquals(expectedResponse.getAmount(), response.getAmount());
                    assertEquals(expectedResponse.getDescription(), response.getDescription());
                });

        // Verificamos que el handler fue llamado con la transacción
        Mockito.verify(transactionHandler, times(1)).registerTransaction(Mockito.any(TransactionRequestDTO.class));
    }
}
