package ec.com.sofka.gateway;

import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.transaction.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TransactionRepository {
    Flux<TransactionDTO> findAll();
    Mono<TransactionDTO> save(TransactionDTO transaction);
    Flux<TransactionDTO> findByAccountNumber(String accountNumber);
}
