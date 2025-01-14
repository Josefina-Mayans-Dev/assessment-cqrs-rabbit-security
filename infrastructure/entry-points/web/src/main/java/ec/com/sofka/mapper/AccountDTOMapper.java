package ec.com.sofka.mapper;

import ec.com.sofka.appservice.account.commands.CreateAccountCommand;
import ec.com.sofka.appservice.account.commands.UpdateAccountCommand;
import ec.com.sofka.appservice.account.queries.responses.AccountResponse;
import ec.com.sofka.data.AccountRequestDTO;
import ec.com.sofka.data.AccountResponseDTO;

public class AccountDTOMapper {
    public static AccountResponseDTO fromEntity(AccountResponse accountResponse) {
        return new AccountResponseDTO(
                accountResponse.getCustomerId(),
                accountResponse.getBalance(),
                accountResponse.getAccountNumber(),
                accountResponse.getAccountHolder(),
                accountResponse.getTransactions());
    }

    public static CreateAccountCommand toEntity(AccountRequestDTO accountRequestDTO) {
        return new CreateAccountCommand(
                accountRequestDTO.getCustomerId(),
                accountRequestDTO.getBalance(),
                accountRequestDTO.getAccountNumber(),
                accountRequestDTO.getAccountHolder());
    }

    // Convierte AccountRequestDTO a UpdateAccountCommand
    public static UpdateAccountCommand accountRequestDTOtoUpdateAccountCommand(AccountRequestDTO requestDTO) {
        return new UpdateAccountCommand(
                requestDTO.getCustomerId(),
                requestDTO.getBalance(),
                requestDTO.getAccountNumber(),
                requestDTO.getAccountHolder()
        );
    }

    public static AccountResponse updateAccountResponseToAccountResponse(AccountResponse response) {
        return new AccountResponse(
                response.getCustomerId(),
                response.getAccountId(),
                response.getBalance(),
                response.getAccountNumber(),
                response.getAccountHolder()
        );
    }
}