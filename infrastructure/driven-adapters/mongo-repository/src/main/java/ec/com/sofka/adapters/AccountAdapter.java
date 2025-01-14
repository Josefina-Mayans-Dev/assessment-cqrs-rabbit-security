package ec.com.sofka.adapters;

import ec.com.sofka.data.AccountEntity;
import ec.com.sofka.database.bank.AccountMongoRepository;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AccountAdapter implements AccountRepository {

    private final AccountMongoRepository repository;
    private final ReactiveMongoTemplate accountMongoTemplate;

    public AccountAdapter(AccountMongoRepository repository, @Qualifier("accountMongoTemplate")  ReactiveMongoTemplate accountMongoTemplate) {
        this.repository = repository;
        this.accountMongoTemplate = accountMongoTemplate;
    }

    @Override
    public Mono<AccountDTO> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber).map(AccountMapper::toDTO);
    }

    @Override
    public Mono<AccountDTO> save(AccountDTO account) {
        AccountEntity accountEntity = AccountMapper.toEntity(account);
        return repository.save(accountEntity).map(AccountMapper::toDTO);
    }

    @Override
    public Mono<AccountDTO> update(AccountDTO account) {
        AccountEntity accountEntity = AccountMapper.toEntity(account);

        return repository.findById(accountEntity.getId())
                .flatMap(found -> {
                    AccountEntity updatedEntity = new AccountEntity(
                            found.getId(),
                            account.getBalance(),
                            account.getAccountNumber(),
                            account.getAccountHolder()

                    );

                    return repository.save(updatedEntity)
                            .map(AccountMapper::toDTO);
                });
    }

    @Override
    public Flux<AccountDTO> findAll() {
        return repository.findAll().map(AccountMapper::toDTO);
    }
}
