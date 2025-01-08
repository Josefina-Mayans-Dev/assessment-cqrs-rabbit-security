package ec.com.sofka.mapper;

import ec.com.sofka.appservice.account.request.CreateAccountRequest;
import ec.com.sofka.appservice.account.responses.AccountResponse;
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

    public static CreateAccountRequest toEntity(AccountRequestDTO accountRequestDTO) {
        return new CreateAccountRequest(
                accountRequestDTO.getCustomerId(),
                accountRequestDTO.getBalance(),
                accountRequestDTO.getAccountNumber(),
                accountRequestDTO.getAccountHolder());
    }
}