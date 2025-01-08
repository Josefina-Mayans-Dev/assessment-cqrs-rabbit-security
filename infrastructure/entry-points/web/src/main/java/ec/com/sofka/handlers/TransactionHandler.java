package ec.com.sofka.handlers;

import ec.com.sofka.appservice.transaction.GetTransactionsByAccountNumberUseCase;
import ec.com.sofka.appservice.transaction.RegisterTransactionUseCase;
import ec.com.sofka.appservice.transaction.request.RegisterTransactionRequest;
import ec.com.sofka.appservice.transaction.responses.TransactionResponse;
import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;
import ec.com.sofka.mapper.AccountDTOMapper;
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

    public Flux<TransactionResponseDTO> getTransactionsByAccount (TransactionRequestDTO request){
        return getTransactionsByAccountNumberUseCase.execute(request.getAccountNumber()).map(AccountDTOMapper::fromEntity);
    }
}