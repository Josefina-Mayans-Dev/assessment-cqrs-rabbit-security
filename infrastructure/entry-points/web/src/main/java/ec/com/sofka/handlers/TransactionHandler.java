package ec.com.sofka.handlers;

import ec.com.sofka.appservice.account.queries.query.GetAccountQuery;
import ec.com.sofka.appservice.transaction.commands.usecases.RegisterTransactionUseCase;
import ec.com.sofka.appservice.transaction.queries.query.GetTransactionsByAccountNumberQuery;
import ec.com.sofka.appservice.transaction.queries.usecases.GetTransactionsByAccountNumberUseCase;
import ec.com.sofka.appservice.transaction.commands.RegisterTransactionCommand;
import ec.com.sofka.data.AccountResponseDTO;
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

   /* public Flux<TransactionResponseDTO> getTransactionsByAccount (TransactionRequestDTO request){
        GetTransactionsByAccountNumberQuery command = new GetTransactionsByAccountNumberQuery(
                request.getCustomerId(),
                request.getAccountNumber()
        );

        return getTransactionsByAccountNumberUseCase.get(command)
                .map(transactionResponse -> new TransactionResponseDTO(
                        transactionResponse.getActionId(),
                        transactionResponse.getAmount(),
                        transactionResponse.getFee(),
                        transactionResponse.getTransactionTypes(),
                        transactionResponse.getDate(),
                        transactionResponse.getDescription(),
                        transactionResponse.getCustomerId()
                ));
    }*/

    public Flux<TransactionResponseDTO> getTransactionsByAccount(GetTransactionsByAccountNumberQuery query) {

        return getTransactionsByAccountNumberUseCase.get(query)  // Obtén la respuesta reactiva
                .flatMapMany(queryResponse -> {
                    return Flux.fromIterable(queryResponse.getMultipleResults() )
                            .map(TransactionDTOMapper::fromEntity);  // Transformar el dominio a DTO
                });

    }


    /*public Mono<TransactionResponseDTO> getTransactionsByAccount(TransactionRequestDTO request) {
        GetTransactionsByAccountNumberQuery command = new GetTransactionsByAccountNumberQuery(request.getCustomerId(), request.getAccountNumber());

        return getTransactionsByAccountNumberUseCase.get(command)
                .flatMap(queryResponse ->
                        Mono.justOrEmpty(queryResponse.getSingleResult()) // Acceder al TransactionResponse
                                .flatMap(transactionResponse ->
                                        getAccountByIdUseCase.get(new GetByElementQuery(transactionResponse.getCustomerId(),
                                                        transactionResponse.getAccountId()))
                                                .map(accountResponse -> TransactionDTOMapper.fromEntity(transactionResponse))
                                )
                );
    }*/
}
