package ec.com.sofka.database.bank;

import ec.com.sofka.data.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserMongoRepository extends ReactiveMongoRepository<UserEntity, String> {
  //  Mono<UserEntity> findByUsername(String username);
    Mono<UserEntity> findByEmail(String email);
}
