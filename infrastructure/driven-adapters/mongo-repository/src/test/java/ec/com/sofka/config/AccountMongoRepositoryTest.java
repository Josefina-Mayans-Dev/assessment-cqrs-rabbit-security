package ec.com.sofka.config;

import ec.com.sofka.TestMongoConfig;
import ec.com.sofka.data.AccountEntity;
import ec.com.sofka.database.bank.AccountMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ActiveProfiles("test")
@DataMongoTest
@AutoConfigureDataMongo
@ContextConfiguration(classes = TestMongoConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountMongoRepositoryTest {

    @Autowired
    private AccountMongoRepository repository;

    private AccountEntity account;

    @BeforeEach
    void setUp() {
        account = new AccountEntity("1", BigDecimal.valueOf(1000.50), "John Doe", "12345678" );
        repository.deleteAll().block(); // Limpiar la base de datos antes de cada prueba
    }
    @Test
    void testSaveAccount() {
        Mono<AccountEntity> saveResult = repository.save(account);

        StepVerifier.create(saveResult)
                .expectNextMatches(savedAccount -> savedAccount.getId().equals(account.getId()) &&
                        savedAccount.getAccountHolder().equals(account.getAccountHolder()) &&
                        savedAccount.getAccountNumber().equals(account.getAccountNumber()) &&
                        savedAccount.getBalance().compareTo(account.getBalance()) == 0 )
                .verifyComplete();
    }

    @Test
    void testFindById() {
        repository.save(account).block();

        Mono<AccountEntity> findResult = repository.findById("1");

        StepVerifier.create(findResult)
                .expectNextMatches(foundAccount -> foundAccount.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void testFindByIdNonExistent() {
        Mono<AccountEntity> findResult = repository.findById("non-existent-id");

        StepVerifier.create(findResult)
                .expectNextCount(0) // No se espera ningún elemento
                .verifyComplete();
    }

    @Test
    void testFindByAccountNumber() {
        repository.save(account).block();

        Mono<AccountEntity> findResult = repository.findByAccountNumber("12345678");

        StepVerifier.create(findResult)
                .expectNextMatches(foundAccount -> foundAccount.getAccountNumber().equals("12345678"))
                .verifyComplete();
    }


    @Test
    void testFindByAccountNumberNonExistent() {
        Mono<AccountEntity> findResult = repository.findByAccountNumber("non-existent-account-number");

        StepVerifier.create(findResult)
                .expectNextCount(0) // No se espera ningún elemento
                .verifyComplete();
    }
    @Test
    void testUpdateAccount() {
        repository.save(account).block();

        Mono<AccountEntity> updateResult = repository.findById("1")
                .flatMap(existingAccount -> {
                    existingAccount = new AccountEntity("1", BigDecimal.valueOf(2000.00), "John Updated", "12345678");
                    return repository.save(existingAccount);
                });

        StepVerifier.create(updateResult)
                .expectNextMatches(updatedAccount -> updatedAccount.getAccountHolder().equals("John Updated") &&
                        updatedAccount.getBalance().compareTo(BigDecimal.valueOf(2000.00)) == 0 )
                .verifyComplete();
    }

    @Test
    void testUpdateNonExistentAccount() {
        Mono<AccountEntity> updateResult = repository.findById("non-existent-id")
                .flatMap(existingAccount -> {
                    existingAccount = new AccountEntity("non-existent-id", "Non Existent", "00000000", BigDecimal.valueOf(500.00), "INACTIVE");
                    return repository.save(existingAccount);
                });

        StepVerifier.create(updateResult)
                .expectNextCount(0) // No se espera ningún elemento ya que no existe
                .verifyComplete();
    }

    @Test
    void testDeleteAccount() {
        repository.save(account).block();

        Mono<Void> deleteResult = repository.deleteById("1");

        StepVerifier.create(deleteResult)
                .verifyComplete();

        StepVerifier.create(repository.findById("1"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testDeleteNonExistentAccount() {
        Mono<Void> deleteResult = repository.deleteById("non-existent-id");

        StepVerifier.create(deleteResult)
                .verifyComplete(); // Debería completarse sin lanzar una excepción
    }
}
