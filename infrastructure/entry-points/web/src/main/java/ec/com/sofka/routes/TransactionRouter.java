package ec.com.sofka.routes;


import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;
import ec.com.sofka.exceptions.AccountNotFoundException;
import ec.com.sofka.exceptions.GlobalExceptionHandler;
import ec.com.sofka.handlers.TransactionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class TransactionRouter {

    private final TransactionHandler transactionHandler;
    private final GlobalExceptionHandler globalExceptionHandler;

    public TransactionRouter(TransactionHandler transactionHandler, GlobalExceptionHandler globalExceptionHandler) {
        this.transactionHandler = transactionHandler;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/transactions",
                    operation = @Operation(
                            tags = {"Transactions"},
                            operationId = "create",
                            summary = "Create a new transaction",
                            description = "Creates a new transaction for the account with the provided id.",
                            requestBody = @RequestBody(
                                    description = "Transaction creation details",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = TransactionRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Transaction registered successfully",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Account with provided account number not found",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Insufficient balance for this transaction",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/transactions/{accountNumber}",
                    operation = @Operation(
                            tags = {"Transactions"},
                            operationId = "getByAccountNumber",
                            summary = "Get all transactions from account by account number ",
                            description = "Retrieve all transactions from a specific account with their respective details",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Transactions retrieved successfully",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "No content found",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> transactionRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/transactions")
                        .and(accept(MediaType.APPLICATION_JSON)), this::registerTransaction)
               .andRoute(RequestPredicates.GET("/transactions/{accountNumber}"), this::getTransactionsByAccount);
    }

    public Mono<ServerResponse> registerTransaction(ServerRequest request){
        return request.bodyToMono(TransactionRequestDTO.class)
                .flatMap(transactionHandler::registerTransaction)
                .flatMap(transactionResponseDTO -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionResponseDTO));
    }

    public Mono<ServerResponse> getTransactionsByAccount(ServerRequest request){
        String accountNumber = request.pathVariable("accountNumber");
        return transactionHandler.getTransactionsByAccount(accountNumber)
                .switchIfEmpty(Mono.error (new AccountNotFoundException("Account not found")))
                .collectList()
                .flatMap(transactionResponseDTO -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionResponseDTO)
                        
                );
    }
}
