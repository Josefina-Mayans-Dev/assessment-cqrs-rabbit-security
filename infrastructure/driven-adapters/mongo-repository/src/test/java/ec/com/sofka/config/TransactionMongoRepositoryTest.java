package ec.com.sofka.config;

import ec.com.sofka.TestMongoConfig;

import ec.com.sofka.data.TransactionEntity;
import ec.com.sofka.database.bank.TransactionMongoRepository;
import ec.com.sofka.utils.enums.TransactionTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ActiveProfiles("test")
@DataMongoTest
@AutoConfigureDataMongo
@ContextConfiguration(classes = TestMongoConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionMongoRepositoryTest {

    @Autowired
    private TransactionMongoRepository transactionRepository;

    private TransactionEntity transaction1;
    private TransactionEntity transaction2;

    @BeforeAll
    void setup() {
        transaction1 = new TransactionEntity(
                "1",
                TransactionTypes.DEPOSIT_ATM,
                BigDecimal.valueOf(100.0),
                BigDecimal.valueOf(2.0),
                LocalDateTime.now(),
                "Deposit ATM",
                "ACC123"

        );
        transaction2 = new TransactionEntity(
                "2",
                TransactionTypes.WITHDRAW_ATM,
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(1.0),
                LocalDateTime.now(),
                "Withdraw from ATM",
                "ACC123"
        );
    }

    @BeforeEach
    void init() {
        transactionRepository.deleteAll().block();
        transactionRepository.saveAll(Flux.just(transaction1, transaction2)).blockLast();
    }

    @Test
    void findById_shouldReturnTransaction_whenTransactionExists() {
        StepVerifier.create(transactionRepository.findById("1"))
                .expectNextMatches(transaction -> transaction.getAmount().equals(BigDecimal.valueOf(100.0))
                        && transaction.getFee().equals(BigDecimal.valueOf(2.0))
                        && transaction.getTransactionTypes() == TransactionTypes.DEPOSIT_ATM
                        && transaction.getAccountId().equals("ACC123"))
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenTransactionDoesNotExist() {
        StepVerifier.create(transactionRepository.findById("99"))
                .verifyComplete();
    }

    @Test
    void findAllByAccountId_shouldReturnTransactions_whenAccountHasTransactions() {
        StepVerifier.create(transactionRepository.findAllByAccountId("ACC123"))
                .expectNextMatches(transaction -> transaction.getId().equals("1"))
                .expectNextMatches(transaction -> transaction.getId().equals("2"))
                .verifyComplete();
    }

    @Test
    void findAllByAccountId_shouldReturnEmpty_whenAccountHasNoTransactions() {
        StepVerifier.create(transactionRepository.findAllByAccountId("NON_EXISTENT_ACC"))
                .verifyComplete();
    }

    @Test
    void save_shouldPersistTransaction() {
        TransactionEntity newTransaction = new TransactionEntity(
                "3",
                TransactionTypes.DEPOSIT_ATM,
                BigDecimal.valueOf(200.0),
                BigDecimal.valueOf(5.0),
                LocalDateTime.now(),
                "Deposit",
                "ACC789"
        );

        StepVerifier.create(transactionRepository.save(newTransaction))
                .expectNextMatches(transaction -> transaction.getId().equals("3")
                        && transaction.getAmount().equals(BigDecimal.valueOf(200.0)))
                     //   && transaction.getNetAmount().equals(BigDecimal.valueOf(195.0)))
                .verifyComplete();

        StepVerifier.create(transactionRepository.findById("3"))
                .expectNextMatches(transaction -> transaction.getTransactionTypes() == TransactionTypes.DEPOSIT_ATM
                        && transaction.getAccountId().equals("ACC789"))
                .verifyComplete();
    }

    @Test
    void delete_shouldRemoveTransaction() {
        StepVerifier.create(transactionRepository.deleteById("1"))
                .verifyComplete();

        StepVerifier.create(transactionRepository.findById("1"))
                .verifyComplete();
    }
}
