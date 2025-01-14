package ec.com.sofka.mapper;

import ec.com.sofka.appservice.transaction.commands.RegisterTransactionCommand;
import ec.com.sofka.appservice.transaction.queries.responses.TransactionResponse;
import ec.com.sofka.data.TransactionRequestDTO;
import ec.com.sofka.data.TransactionResponseDTO;

public class TransactionDTOMapper {
    public static TransactionResponseDTO fromEntity(TransactionResponse transactionResponse) {
        return new TransactionResponseDTO(
                transactionResponse.getActionId(),
                transactionResponse.getFee(),
                transactionResponse.getAmount(),
                transactionResponse.getTransactionTypes(),
                transactionResponse.getDate(),
                transactionResponse.getDescription(),
                transactionResponse.getCustomerId()
        );
    }

    public static RegisterTransactionCommand toEntity(TransactionRequestDTO transactionRequestDTO) {
        return new RegisterTransactionCommand(
                transactionRequestDTO.getAmount(),
                transactionRequestDTO.getTransactionTypes(),
                transactionRequestDTO.getAccountNumber(),
                transactionRequestDTO.getCustomerId(),
                transactionRequestDTO.getDescription());
    }
}
