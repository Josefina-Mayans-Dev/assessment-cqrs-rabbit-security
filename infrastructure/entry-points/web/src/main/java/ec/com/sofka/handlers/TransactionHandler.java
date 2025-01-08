package ec.com.sofka.handlers;

import ec.com.sofka.appservice.transaction.GetTransactionsByAccountNumberUseCase;
import ec.com.sofka.appservice.transaction.RegisterTransactionUseCase;
import ec.com.sofka.appservice.transaction.request.GetTransactionsByAccountNumberRequest;
import ec.com.sofka.appservice.transaction.request.RegisterTransactionRequest;
import ec.com.sofka.appservice.transaction.responses.TransactionResponse;
import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;
import ec.com.sofka.mapper.AccountDTOMapper;
import ec.com.sofka.mapper.TransactionDTOMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final GetTransactionsByAccountNumberUseCase getTransactionsByAccountNumberUseCase;

    public TransactionHandler(RegisterTransactionUseCase registerTransactionUseCase,
                              GetTransactionsByAccountNumberUseCase getTransactionsByAccountNumberUseCase) {
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.getTransactionsByAccountNumberUseCase = getTransactionsByAccountNumberUseCase;
    }

    public Mono<TransactionResponseDTO> registerTransaction(TransactionRequestDTO request) {
        return Mono.just(request)
                .map(TransactionDTOMapper::toEntity)
                .flatMap(registerTransactionUseCase::execute)
                .map(TransactionDTOMapper::fromEntity)
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Error while processing transaction", e));
                })
                .map(response -> new TransactionResponseDTO(
                        response.getActionId(),
                        response.getAmount(),
                        response.getFee(),
                        response.getTransactionTypes(),
                        response.getDate(),
                        response.getCustomerId(),
                        response.getDescription()));
    }

    public Flux<TransactionResponseDTO> getTransactionsByAccount (TransactionRequestDTO request){
        GetTransactionsByAccountNumberRequest command = new GetTransactionsByAccountNumberRequest(
                request.getCustomerId(),
                request.getAccountNumber()
        );

        return getTransactionsByAccountNumberUseCase.execute(command)
                .map(transactionResponse -> new TransactionResponseDTO(
                        transactionResponse.getActionId(),
                        transactionResponse.getAmount(),
                        transactionResponse.getFee(),
                        transactionResponse.getTransactionTypes(),
                        transactionResponse.getDate(),
                        transactionResponse.getDescription(),
                        transactionResponse.getCustomerId()
                ));
    }
}