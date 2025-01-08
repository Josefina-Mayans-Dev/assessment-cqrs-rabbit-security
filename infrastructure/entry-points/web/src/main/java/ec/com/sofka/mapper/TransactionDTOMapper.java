package ec.com.sofka.mapper;

import ec.com.sofka.appservice.transaction.request.RegisterTransactionRequest;
import ec.com.sofka.appservice.transaction.responses.TransactionResponse;
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

    public static RegisterTransactionRequest toEntity(TransactionRequestDTO transactionRequestDTO) {
        return new RegisterTransactionRequest(
                transactionRequestDTO.getAmount(),
                transactionRequestDTO.getTransactionTypes(),
                transactionRequestDTO.getAccountNumber(),
                transactionRequestDTO.getCustomerId(),
                transactionRequestDTO.getDescription());
    }
}
