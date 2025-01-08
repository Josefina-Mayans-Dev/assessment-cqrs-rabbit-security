package ec.com.sofka.database.bank;

import ec.com.sofka.data.LogEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LogMongoRepository extends ReactiveMongoRepository<LogEntity, String> {
}
