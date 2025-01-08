package ec.com.sofka.gateway;

import ec.com.sofka.log.Log;
import reactor.core.publisher.Mono;

public interface LogRepository {
    Mono<Void> saveLog (Log log);
}
