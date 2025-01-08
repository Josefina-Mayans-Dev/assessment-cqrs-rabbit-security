package ec.com.sofka.handlers;

import ec.com.sofka.appservice.transaction.RegisterTransactionUseCase;
import ec.com.sofka.appservice.transaction.request.RegisterTransactionRequest;
import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {
    private final RegisterTransactionUseCase registerTransactionUseCase;

    public TransactionHandler(RegisterTransactionUseCase registerTransactionUseCase) {
        this.registerTransactionUseCase = registerTransactionUseCase;
    }

    public Mono<TransactionResponseDTO> registerTransaction(TransactionRequestDTO request) {
        return registerTransactionUseCase.execute(
                        new RegisterTransactionRequest(
                                request.getAmount(),
                                request.getTransactionTypes(),
                                request.getAccountNumber(),
                                request.getDescription(),
                                request.getCustomerId()
                        ))
                .map(response -> new TransactionResponseDTO(
                        response.getActionId(),
                        response.getAmount(),
                        response.getFee(),
                        response.getTransactionTypes(),
                        response.getDate(),
                        response.getCustomerId(),
                        response.getDescription()));
    }
}