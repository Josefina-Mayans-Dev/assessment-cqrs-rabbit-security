package ec.com.sofka.handlers;

import ec.com.sofka.appservice.account.CreateAccountUseCase;
import ec.com.sofka.appservice.account.GetAllAccountsUseCase;
import ec.com.sofka.appservice.account.UpdateAccountUseCase;
import ec.com.sofka.appservice.account.request.CreateAccountRequest;
import ec.com.sofka.appservice.account.request.UpdateAccountRequest;
import ec.com.sofka.data.AccountRequestDTO;
import ec.com.sofka.data.AccountResponseDTO;
import ec.com.sofka.mapper.AccountDTOMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AccountHandler {
    private final CreateAccountUseCase createAccountUseCase;
    private final GetAllAccountsUseCase getAllAccountsUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;

    public AccountHandler(CreateAccountUseCase createAccountUseCase, GetAllAccountsUseCase getAllAccountsUseCase,
    UpdateAccountUseCase updateAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAllAccountsUseCase = getAllAccountsUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
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

    public Flux<AccountResponseDTO> getAllAccounts() {

        return getAllAccountsUseCase.get().map(AccountDTOMapper::fromEntity);

    }


    public Mono<AccountResponseDTO> updateAccount(AccountRequestDTO request){
        return updateAccountUseCase.execute(
                        new UpdateAccountRequest(
                                request.getCustomerId(),
                                request.getBalance(),
                                request.getAccountNumber(),
                                request.getAccountHolder()
                        ))
                .map(response -> new AccountResponseDTO(
                        response.getCustomerId(),
                        response.getBalance(),
                        response.getAccountHolder(),
                        response.getAccountNumber(),
                        response.getTransactions()
                ));
    }

}
