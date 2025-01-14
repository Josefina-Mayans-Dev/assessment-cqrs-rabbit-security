package ec.com.sofka.handlers;

import ec.com.sofka.appservice.account.commands.usecases.CreateAccountUseCase;
import ec.com.sofka.appservice.account.queries.query.GetAccountQuery;
import ec.com.sofka.appservice.account.queries.usecases.GetAllAccountsUseCase;
import ec.com.sofka.appservice.account.commands.usecases.UpdateAccountUseCase;
import ec.com.sofka.appservice.account.commands.CreateAccountCommand;
import ec.com.sofka.appservice.account.commands.UpdateAccountCommand;
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
        return createAccountUseCase.execute(
                        new CreateAccountCommand(
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

    public Flux<AccountResponseDTO> getAllAccounts(GetAccountQuery query) {

        return getAllAccountsUseCase.get(query)  // Obtén la respuesta reactiva
                .flatMapMany(queryResponse -> {
                    return Flux.fromIterable(queryResponse.getMultipleResults() )
                            .map(AccountDTOMapper::fromEntity);  // Transformar el dominio a DTO
                });

    }


    /*public Mono<AccountResponseDTO> updateAccount(AccountRequestDTO request){
        return updateAccountUseCase.execute(
                        new UpdateAccountCommand(
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
    }*/

    public Mono<AccountResponseDTO> updateAccount(AccountRequestDTO request) {
        return updateAccountUseCase.execute(AccountDTOMapper.accountRequestDTOtoUpdateAccountCommand(request))
                .map(AccountDTOMapper::updateAccountResponseToAccountResponse)
                .map(AccountDTOMapper::fromEntity);
    }

}
