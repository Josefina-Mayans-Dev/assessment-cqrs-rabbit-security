package ec.com.sofka.handlers;

import ec.com.sofka.appservice.account.CreateAccountUseCase;
import ec.com.sofka.appservice.account.request.CreateAccountRequest;
import ec.com.sofka.data.AccountRequestDTO;
import ec.com.sofka.data.AccountResponseDTO;
import ec.com.sofka.mapper.AccountDTOMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountHandler {
    private final CreateAccountUseCase createAccountUseCase;

    public AccountHandler(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    public Mono<AccountResponseDTO> createAccount(AccountRequestDTO request) {
        // Ejecutar el caso de uso y mapear la respuesta a un ResponseDTO
        return createAccountUseCase.execute(
                        new CreateAccountRequest(
                                request.getCustomerId(),
                                request.getBalance(),
                                request.getAccountNumber(),
                                request.getAccountHolder()
                        ))
                .map(response -> new AccountResponseDTO(
                        response.getCustomerId(),
                        response.getBalance(),
                        response.getAccountNumber(),
                        response.getAccountHolder(),
                        response.getTransactions()));
    }
}
