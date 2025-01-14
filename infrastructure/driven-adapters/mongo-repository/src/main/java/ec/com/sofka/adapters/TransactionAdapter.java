package ec.com.sofka.adapters;

import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.database.bank.TransactionMongoRepository;
import ec.com.sofka.gateway.TransactionRepository;
import ec.com.sofka.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TransactionAdapter implements  TransactionRepository{

    private final TransactionMongoRepository transactionMongoRepository;
    private final ReactiveMongoTemplate accountMongoTemplate;

    public TransactionAdapter(TransactionMongoRepository transactionMongoRepository, @Qualifier("accountMongoTemplate")ReactiveMongoTemplate accountMongoTemplate) {
        this.transactionMongoRepository = transactionMongoRepository;
        this.accountMongoTemplate = accountMongoTemplate;
    }


    @Override
    public Flux<TransactionDTO> findAll() {
        return transactionMongoRepository.findAll().map(TransactionMapper::toTransaction);
    }

    @Override
    public Mono<TransactionDTO> save(TransactionDTO transaction) {
        return transactionMongoRepository.save(TransactionMapper.toTransactionEntity(transaction))
                .map(TransactionMapper::toTransaction);
    }

   /* @Override
    public Flux<TransactionDTO> findByAccountNumber(String accountNumber) {
        return null;
        return transactionMongoRepository.findByAccountNumber(accountNumber)
                .map(TransactionMapper::toTransaction);
    }*/

    @Override
    public Flux<TransactionDTO> getAllByAccountId(String accountId) {
        return transactionMongoRepository.findAllByAccountId(accountId).map(TransactionMapper::toTransaction);
    }
}
