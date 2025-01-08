package ec.com.sofka.adapters;

import ec.com.sofka.log.Log;
import ec.com.sofka.database.bank.LogMongoRepository;
import ec.com.sofka.data.LogEntity;
import ec.com.sofka.gateway.LogRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LogAdapter implements LogRepository {
    private final LogMongoRepository logMongoRepository;

    public LogAdapter(LogMongoRepository logMongoRepository){
        this.logMongoRepository = logMongoRepository;
    }

    @Override
    public Mono<Void> saveLog(Log log) {
        LogEntity logEntity = new LogEntity(log.getId(), log.getMessage(), log.getEntity(), log.getTimestamp());
        return logMongoRepository.save(logEntity).then();
    }
}
