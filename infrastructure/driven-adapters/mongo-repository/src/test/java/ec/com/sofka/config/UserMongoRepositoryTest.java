package ec.com.sofka.config;

import ec.com.sofka.TestMongoConfig;
import ec.com.sofka.data.AccountEntity;
import ec.com.sofka.data.UserEntity;
import ec.com.sofka.database.bank.UserMongoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataMongoTest
@AutoConfigureDataMongo
@ContextConfiguration(classes = TestMongoConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserMongoRepositoryTest {
    @Autowired
    private UserMongoRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll().subscribe();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll().subscribe();
    }

    @Test
    @DisplayName("Should find a user on the database with the same email")
    void findByEmail() {
        UserEntity userEntity  = new UserEntity();
        userEntity.setEmail("emailTest");
        Mono<UserEntity> userSaved = userRepository.save(userEntity);

        StepVerifier.create(userSaved)
                .expectNext(userEntity)
                .verifyComplete();


        Mono<UserEntity> userFound = userRepository.findByEmail(userEntity.getEmail());

        StepVerifier.create(userFound)
                .expectNextMatches(user -> user.getEmail().equals("emailTest"))
                .verifyComplete();


    }

    @Test
    @DisplayName("Should NOT found a user on the database with the same email")
    void findByUsername_error() {
        Mono<UserEntity> userFound = userRepository.findByEmail("emailError");

        StepVerifier.create(userFound)
                .expectNextCount(0)
                .verifyComplete();
    }
}
